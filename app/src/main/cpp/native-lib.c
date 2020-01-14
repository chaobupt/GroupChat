/*
* jpegtran.c
* This file provides lossless transcoding between different JPEG file formats.
* It alsoprovides some lossless and sort-of-lossless transformations of JPEG data.
*/
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include "include/jpeglib.h"
#include "include/cdjpeg.h"        /* Common decls for cjpeg/djpeg applications */
#include "include/transupp.h"        /* Support routines for jpegtran */
#include "include/jversion.h"        /* for version message */

static char * scaleoption;	/* -scale switch */
static JCOPY_OPTION copyoption;	/* -copy switch */
static jpeg_transform_info transformoption; /* image transformation options */

/*scrambling ROIs*/
LOCAL(void)
	jpeg_scrambling(struct jpeg_decompress_struct srcinfo,jvirt_barray_ptr* src_coef_arrays, int key, int left,int top,int right,int bottom)
{
	JBLOCKARRAY rowPtrs[MAX_COMPONENTS];
	int rannum;
	int compNum,rowNum,blockNum,i;
	int seed=key;
	srand(seed);
	for (compNum=0; compNum < 1; compNum++) {
		for (rowNum=top/8; rowNum <bottom/8; rowNum++) {
			// 指向dct值的虚拟数组的指针
			/*JMETHOD(JBLOCKARRAY, access_virt_barray, (j_common_ptr cinfo,jvirt_barray_ptr ptr,JDIMENSION start_row,JDIMENSION num_rows, boolean writable))*/
			rowPtrs[compNum] = ((&srcinfo)->mem->access_virt_barray)((j_common_ptr) &srcinfo, src_coef_arrays[compNum],rowNum, (JDIMENSION) 1, FALSE);
			// 循环遍历这些块以获得dct值
			for (blockNum=left/8; blockNum <right/8; blockNum++){
				for (i=0; i<DCTSIZE2; i++){
					rannum=rand()%2;
					if(rannum==1)
						rowPtrs[compNum][0][blockNum][i] =-(rowPtrs[compNum][0][blockNum][i]);
				}
			}
		}
	}
}

/*descrambling*/
LOCAL(void)
	jpeg_descrambling(struct jpeg_decompress_struct srcinfo,jvirt_barray_ptr* src_coef_arrays,int key,int* face)
{
	jpeg_scrambling(srcinfo,src_coef_arrays,key,face[0],face[1],face[2],face[3]);
}


int generateJPEG( const char* srcfileName, int size, int faceData[][5], const char* outfileName) {
	scaleoption = NULL;
	copyoption = JCOPYOPT_DEFAULT;
	transformoption.transform = JXFORM_NONE;
	transformoption.perfect = FALSE;
	transformoption.trim = FALSE;
	transformoption.force_grayscale = FALSE;
	transformoption.crop = FALSE;


	struct jpeg_decompress_struct srcinfo;
	struct jpeg_compress_struct dstinfo;
	struct jpeg_error_mgr jsrcerr, jdsterr;

	jvirt_barray_ptr * src_coef_arrays;
	jvirt_barray_ptr * dst_coef_arrays;

	FILE * fr;
	FILE * fw;

	/* Initialize the JPEG decompression object with default error handling. */
	srcinfo.err = jpeg_std_error(&jsrcerr);
	jpeg_create_decompress(&srcinfo);
	/* Initialize the JPEG compression object with default error handling. */
	dstinfo.err = jpeg_std_error(&jdsterr);
	jpeg_create_compress(&dstinfo);

	jsrcerr.trace_level = jdsterr.trace_level;
	srcinfo.mem->max_memory_to_use = dstinfo.mem->max_memory_to_use;

	/*************************************读文件***************************************************/
    /* Open the input file. */
    fr = fopen(srcfileName, "rb");
    if(fr==NULL){
        return 0;
    }
	/* Specify data source for decompression */
	jpeg_stdio_src(&srcinfo, fr);

	/* Enable saving of extra markers that we want to copy */
	jcopy_markers_setup(&srcinfo, copyoption);
    //jpeg_save_markers(&srcinfo, JPEG_COM, 0xFFFF);

	/* Read file header */
	(void) jpeg_read_header(&srcinfo, TRUE);


#if TRANSFORMS_SUPPORTED
	/* Fail right away if -perfect is given and transformation is not perfect.
	*/
    jtransform_request_workspace(&srcinfo, &transformoption);
#endif

	/* Read source file as DCT coefficients  jvirt_barray_ptr *  jpeg_read_coefficients (j_decompress_ptr cinfo);*/
	src_coef_arrays = jpeg_read_coefficients(&srcinfo);

	//Scrambling
	//jpeg_scrambling(srcinfo,src_coef_arrays,159,81,44,124,103);
	for(int i=0;i<size;i++){
	 jpeg_scrambling(srcinfo,src_coef_arrays,faceData[i][0],faceData[i][1],faceData[i][2],faceData[i][3],faceData[i][4]);
	}

	/*Descrambling*/
   // jpeg_descrambling(srcinfo,src_coef_arrays,key,face);

	/* Initialize destination compression parameters from source values 从源值初始化目标压缩参数*/
	jpeg_copy_critical_parameters(&srcinfo, &dstinfo);

#if TRANSFORMS_SUPPORTED
	dst_coef_arrays = jtransform_adjust_parameters(&srcinfo, &dstinfo,
		src_coef_arrays,
		&transformoption);
#else
	dst_coef_arrays = src_coef_arrays;
#endif

	fclose(fr);

    /*************************************写文件***************************************************/
	/* Open the output file. */
	fw = fopen(outfileName, "wb");
    if(fw==NULL){
        return 0;
    }

	/* Specify data destination for compression */
	jpeg_stdio_dest(&dstinfo, fw);
	/* Start compressor (note no image data is actually written here) */
	jpeg_write_coefficients(&dstinfo, dst_coef_arrays);

	/* Copy to the output file any extra markers that we want to preserve */
	jcopy_markers_execute(&srcinfo, &dstinfo, copyoption);
	//jcopy_markers_execute(&srcinfo, &dstinfo, 0xFFFF);

    /* Execute image transformation, if any */
#if TRANSFORMS_SUPPORTED
    jtransform_execute_transformation(&srcinfo, &dstinfo,
        src_coef_arrays,
        &transformoption);
#endif

	/* Finish compression and release memory */
	jpeg_finish_compress(&dstinfo);
	jpeg_destroy_compress(&dstinfo);
	(void) jpeg_finish_decompress(&srcinfo);
	jpeg_destroy_decompress(&srcinfo);

	/* Close output file, if we opened it */
	fclose(fw);

	/* All done. */
	return 1;
}


JNIEXPORT jstring JNICALL
Java_com_example_groupchat_JniUtils_scramblingBitmap
		(JNIEnv *env, jclass jclass, jstring srcFilePath, jobjectArray faceDatas, jstring outFilePath){

 //把jobjectArray 转换为 jint[][]
  jint i,j;
  jarray myarray;
  int face[2][5];

  int size = (*env)->GetArrayLength(env, faceDatas);//获得行数
  int col=0;
  for (i = 0; i < size && i<2; i++){
      myarray = ((*env)->GetObjectArrayElement(env, faceDatas, i));
      col =(*env)->GetArrayLength(env, myarray); //获得列数

      jint *coldata = (*env)->GetIntArrayElements(env, (jintArray)myarray, 0 );
      for (j=0; j<col&& j<5; j++) {
          face[i][j] = coldata[j];
      }
      (*env)->ReleaseIntArrayElements(env, (jintArray)myarray, coldata,0 );
  }

    const char *srcfile = (*env)->GetStringUTFChars(env,srcFilePath, 0);
    const char *outfile = (*env)->GetStringUTFChars(env,outFilePath, 0);
    //LOGE("------ 文件目录 %s", filepath);

	int resultCode = generateJPEG(srcfile,size, face,outfile);
	if(resultCode == 0) {
		jstring result = (*env)->NewStringUTF(env,"0");
		return result;
	}
	return (*env)->NewStringUTF(env,"1");
}






