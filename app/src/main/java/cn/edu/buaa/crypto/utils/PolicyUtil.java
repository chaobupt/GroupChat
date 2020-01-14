package cn.edu.buaa.crypto.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.edu.buaa.crypto.access.parser.ParserUtils;

public class PolicyUtil {
	
	/**
	 * 获取value十进制的的二进制数的位数
	 * @param value
	 * @return
	 */
	
	public static Map<String, Integer> getComparableAttributeBinaryLength(String value){
		Map<String, Integer> cabl = new HashMap<String, Integer>();
		value = value.trim();
		value = value.replaceAll("\\(", "( ");
		value = value.replaceAll("\\)", " )");
		String[] valueSplit = value.split(" ");
		for(String segment : valueSplit) {
			if(segment.contains(":")) {
				String[] segmentSplit = segment.split(":");
				long decimal = Long.parseLong(segmentSplit[1]);
				String binary = Long.toBinaryString(decimal);
				cabl.put(segmentSplit[0], binary.length());
			}
		}
		return cabl;
	}

	/**
	 * 将ts的策略-->Y M D的策略
	 * @param policy
	 * @param binaryLength
	 * @return
	 */
	public static String ts2YMDHMS(String policy, Map<String, Integer> binaryLength) {
		policy = policy.trim();
		policy = policy.replaceAll("\\(", "( ");
        policy = policy.replaceAll("\\)", " )");
        
        String[] segmentSplit = policy.split("<");
        StringBuilder sb = new StringBuilder();
        sb.append("( ");
        if(segmentSplit[0].equals("ts")) {
			String[] segmentSplitTs = segmentSplit[1].split("-"); //2019 9 30 10 20 10
			
			sb.append("Ys").append("<").append(segmentSplitTs[0]).append(" or ").append("( ")
			.append("Ys").append("=").append(segmentSplitTs[0]).append(" and ").append("( ")
			.append("Ms").append("<").append(segmentSplitTs[1]).append(" or ").append("( ")
			.append("Ms").append("=").append(segmentSplitTs[1]).append(" and ").append("( ")
			.append("Ds").append("<").append(segmentSplitTs[2]).append(" or ").append("( ")
			.append("Ds").append("=").append(segmentSplitTs[2]).append(" and ").append("( ")
			
			.append("hs").append("<").append(segmentSplitTs[3]).append(" or ").append("( ")
			.append("hs").append("=").append(segmentSplitTs[3]).append(" and ").append("( ")
			.append("ms").append("<").append(segmentSplitTs[4]).append(" or ").append("( ")
			.append("ms").append("=").append(segmentSplitTs[4]).append(" and ").append("( ")
			.append("ss").append("<").append(segmentSplitTs[5]).append(" or ")
			.append("ss").append("=").append(segmentSplitTs[5])
			.append(" )").append(" )").append(" )").append(" )").append(" )").append(" )").append(" )").append(" )").append(" )").append(" )");	
			
			
//			sb.append("Ys").append("<").append(segmentSplitTs[0]).append(" or ").append("( ")
//			.append("Ys").append("=").append(segmentSplitTs[0]).append(" and ").append("( ")
//			.append("Ms").append("<").append(segmentSplitTs[1]).append(" or ").append("( ")
//			.append("Ms").append("=").append(segmentSplitTs[1]).append(" and ").append("( ")
//			.append("Ds").append("<").append(segmentSplitTs[2]).append(" or ")
//			.append("Ds").append("=").append(segmentSplitTs[2])
//			.append(" )").append(" )").append(" )").append(" )");	
		
        }
        sb.append(" )");
		return sb.toString();
		
	}
	
	/**
	 * 将te的策略-->Y M D的策略
	 * @param policy
	 * @param binaryLength
	 * @return
	 */
	public static String te2YMDHMS(String policy, Map<String, Integer> binaryLength) {
		policy = policy.trim();
		policy = policy.replaceAll("\\(", "( ");
        policy = policy.replaceAll("\\)", " )");
        
        String[] segmentSplit = policy.split(">");
        StringBuilder sb = new StringBuilder();
        sb.append("( ");
        if(segmentSplit[0].equals("te")) {
			String[] segmentSplitTe = segmentSplit[1].split("-"); //2019 9 15
			
			sb.append("Ye").append(">").append(segmentSplitTe[0]).append(" or ").append("( ")
			.append("Ye").append("=").append(segmentSplitTe[0]).append(" and ").append("( ")
			.append("Me").append(">").append(segmentSplitTe[1]).append(" or ").append("( ")
			.append("Me").append("=").append(segmentSplitTe[1]).append(" and ").append("( ")
			.append("De").append(">").append(segmentSplitTe[2]).append(" or ").append("( ")
			.append("De").append("=").append(segmentSplitTe[2]).append(" and ").append("( ")
			.append("he").append(">").append(segmentSplitTe[3]).append(" or ").append("( ")
			.append("he").append("=").append(segmentSplitTe[3]).append(" and ").append("( ")
			.append("me").append(">").append(segmentSplitTe[4]).append(" or ").append("( ")
			.append("me").append("=").append(segmentSplitTe[4]).append(" and ").append("( ")
			.append("se").append(">").append(segmentSplitTe[5]).append(" or ")
			.append("se").append("=").append(segmentSplitTe[5])
			.append(" )").append(" )").append(" )").append(" )").append(" )").append(" )").append(" )").append(" )").append(" )").append(" )");	
					
//			sb.append("Ye").append(">").append(segmentSplitTe[0]).append(" or ").append("( ")
//			.append("Ye").append("=").append(segmentSplitTe[0]).append(" and ").append("( ")
//			.append("Me").append(">").append(segmentSplitTe[1]).append(" or ").append("( ")
//			.append("Me").append("=").append(segmentSplitTe[1]).append(" and ").append("( ")
//			.append("De").append(">").append(segmentSplitTe[2]).append(" or ")
//			.append("De").append("=").append(segmentSplitTe[2])
//			.append(" )").append(" )").append(" )").append(" )");	
		
		}
        sb.append(" )");
		return sb.toString();
		
	}
	
	
	/**
	 * 将policy-->采用0/1-encoding的策略
	 * @param policy
	 * @param binaryLength
	 * @return
	 */

	public static String policyReplace(String policy, Map<String, Integer> binaryLength) {
		policy = policy.trim();
		policy = policy.replaceAll("\\(", "( ");
        policy = policy.replaceAll("\\)", " )");
		String[] policySplit = policy.split(" ");
		for(int i = 0; i < policySplit.length; i++) {
			String segment = policySplit[i];
			if(segment.contains("<=")) {
				String[] segmentSplit = segment.split("<=");
				
				String str =segmentSplit[1].replace(";", " ");
				SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
				Date date = new Date();
				
				try {
					date= format.parse(str);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				long decimal = date.getTime();
				//System.out.println("ts: "+decimal);
				
				String binary = decimal2Binary(decimal, binaryLength.get(segmentSplit[0]));
				Set<String> b1_encoding = get1_encoding(binary);
				Set<String> extendAttributeSet = getExtendAttribute(segmentSplit[0], "<x", b1_encoding);
				StringBuilder sb = new StringBuilder();
				sb.append(segmentSplit[0]).append("=").append(decimal);
				extendAttributeSet.add(sb.toString());
				String subtree = generateSubtree(extendAttributeSet);
				policySplit[i] = subtree;
			}
			if(segment.contains(">=")) {
				long decimal;
				String[] segmentSplit = segment.split(">=");
				if(segmentSplit[0].equals("te")) {
					String str =segmentSplit[1].replace(";", " ");

					SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
					Date date = new Date();
					
					try {
						date= format.parse(str);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					decimal = date.getTime();
					//System.out.println("te: "+decimal);
			
				}else {
					decimal =Integer.parseInt(segmentSplit[1]);
				}
				
				String binary = decimal2Binary(decimal, binaryLength.get(segmentSplit[0]));
				Set<String> b0_encoding = get0_encoding(binary);
				Set<String> extendAttributeSet = getExtendAttribute(segmentSplit[0], ">x", b0_encoding);
				StringBuilder sb = new StringBuilder();
				sb.append(segmentSplit[0]).append("=").append(decimal);
				extendAttributeSet.add(sb.toString());
				String subtree = generateSubtree(extendAttributeSet);
				policySplit[i] = subtree;
			}
		}
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i <policySplit.length; i++) {
			sb.append(policySplit[i]).append(" ");
		}
		return sb.toString();
	}

	public static String[] attributeReplace(String[] attributes, Map<String, Integer> binaryLength) {
		Set<String> attributeSet = new HashSet<>();
		for(String attribute : attributes) {
			attribute = attribute.trim();
			if(attribute.contains("=")) {
				String[] attributeSplit = attribute.split("=");
				long decimal;
				
				if(attributeSplit[0].equals("scl")) {
					decimal =Integer.parseInt(attributeSplit[1]);
				}else {
					SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
					Date date = new Date();
					try {
						date = format.parse(attributeSplit[1]);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					decimal = date.getTime();
					//System.out.println("时间A： "+decimal);
				}
					
				String binary = decimal2Binary(decimal, binaryLength.get(attributeSplit[0]));
				StringBuilder sb = new StringBuilder();
				sb.append(attributeSplit[0]).append("=").append(decimal);
				attributeSet.add(sb.toString());
				Set<String> b0_encoding = get0_encoding(binary);
				Set<String> extend0AttributeSet = getExtendAttribute(attributeSplit[0], "<x", b0_encoding);
				attributeSet.addAll(extend0AttributeSet);
				Set<String> b1_encoding = get1_encoding(binary);
				Set<String> extend1AttributeSet = getExtendAttribute(attributeSplit[0], ">x", b1_encoding);
				attributeSet.addAll(extend1AttributeSet);
			}else {
				attributeSet.add(attribute);
			}
		}	
		return attributeSet.toArray(new String[attributeSet.size()]);
	}
	
	
	public static String decimal2Binary(int decimal, int length) {
		String binary = Integer.toBinaryString(decimal);
		while(binary.length()<length) {
			StringBuilder sb = new StringBuilder();
			sb.append("0").append(binary);
			binary = sb.toString();
		}
		return binary;
	}
	
	public static String decimal2Binary(long decimal, int length) {
		String binary = Long.toBinaryString(decimal);
		while(binary.length()<length) {
			StringBuilder sb = new StringBuilder();
			sb.append("0").append(binary);
			binary = sb.toString();
		}
		return binary;
	}

	
	public static Set<String> get0_encoding(String binary) {
		Set<String> b0_encoding = new HashSet<>();
		char[] charArrayB = binary.toCharArray();
		for(int i = 0; i < charArrayB.length; i++) {
			if(charArrayB[i] == '0') {
				StringBuilder sb = new StringBuilder();
				sb.append(charArrayB, 0, i).append('1');
				b0_encoding.add(sb.toString());
			}
		}
		
		return b0_encoding;
	}
	
	public static Set<String> get1_encoding(String binary){
		Set<String> b1_encoding = new HashSet<>();
		char[] charArrayB = binary.toCharArray();
		for(int i = 0; i <charArrayB.length; i++) {
			if(charArrayB[i] == '1') {
				StringBuilder sb = new StringBuilder();
				sb.append(charArrayB, 0, i+1);
				b1_encoding.add(sb.toString());
			}
		}
		return b1_encoding;
	}
	
	public static Set<String> getExtendAttribute(String attribute, String symbol, Set<String> encodingSet){
		Set<String> extendAttributeSet = new HashSet<>();
		for(String encoding : encodingSet) {
			StringBuilder sb = new StringBuilder();
			sb.append(attribute).append("||").append(symbol).append("||").append(encoding);
			extendAttributeSet.add(sb.toString());
		}
		return extendAttributeSet;
	}
	
	public static String generateSubtree(Set<String> extendAttributeSet) {
		StringBuilder sb = new StringBuilder();
		sb.append("( ");
		int j = 0;
		for(String extendAttribute : extendAttributeSet) {
			
			if(++j < extendAttributeSet.size()) {
				sb.append(extendAttribute).append(" or ");
			}else {
				sb.append(extendAttribute);
			}
		}
		sb.append(" )");
		return sb.toString();
	}
	
	
}
