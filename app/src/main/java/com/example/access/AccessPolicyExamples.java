package com.example.access;

/**
 * Created by Weiran Liu on 2016/11/17.
 *
 * Access policy examples, used for testing AccessControlEngine and Attribute-Based Encryption schemes.
 */
public class AccessPolicyExamples {

    public static final String access_policy_example_1 = "0 and 1 and (2 or 3)";
    public static final String[] access_policy_example_1_satisfied_1 = new String[] {"0", "1", "2"};
    public static final String[] access_policy_example_1_satisfied_2 = new String[] {"0", "1", "2", "3"};
    public static final String[] access_policy_example_1_unsatisfied_1 = new String[] {"1", "2", "3"};

    public static final String access_policy_example_2 = "((0 and 1 and 2) and (3 or 4 or 5) and (6 and 7 and (8 or 9 or 10 or 11)))";
    public static final String[] access_policy_example_2_satisfied_1 = new String[] {"0", "1", "2", "4", "6", "7", "10"};
    public static final String[] access_policy_example_2_satisfied_2 = new String[] {"0", "1", "2", "5", "4", "6", "7", "8", "9", "10", "11"};
    public static final String[] access_policy_example_2_unsatisfied_1 = new String[] {"0", "1", "2", "6", "7", "10"};
    public static final String[] access_policy_example_2_unsatisfied_2 = new String[] {"0", "1", "2", "4", "6", "10"};
    public static final String[] access_policy_example_2_unsatisfied_3 = new String[] {"0", "1", "2", "3", "6", "7"};
  
    
    
    //支持门限方案
    public static final int[][] access_policy_threshold_example_1_tree = {
            {3, 2, 1, 2, 3},
            {3, 2, -1, -2, -3},
            {3, 2, -4, -5, -6},
            {3, 2, -7, -8, 4},
            {4, 3, -9, -10, -11, -12},
    };
    public static final String[] access_policy_threshold_example_1_rho = new String[] {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10","11",
    };
    public static final String[] access_policy_threshold_example_1_satisfied01 = new String[] {
            "0", "1", "3", "4",
    };
    public static final String[] access_policy_threshold_example_1_satisfied02 = new String[] {
            "0", "1", "6", "7",
    };
    public static final String[] access_policy_threshold_example_1_unsatisfied01 = new String[] {
            "0", "3", "6", "8", "9",
    };
    public static final String[] access_policy_threshold_example_1_unsatisfied02 = new String[] {
            "0", "3", "6", "7",
    };

    public static final int[][] access_policy_threshold_example_2_tree = {
            {2,2,1,2}, //the root node 0 is a 2of2 threshold and its children are nodes 1 and 2 (at rows 1 and 2) <br>
            {2,2,3,4}, //node 1 is a 1of2 threshold and its children are nodes 3 and 4 <br>
            {4,3,-7,-8,-9,-10}, //node 2 note that -5 here correponds to index of attribute E in the alphabet<br>
            {2,2,-2,5}, //node 3 <br>
            {3,2,-4,-5,-6}, //node 4 <br>
            {2,1,-1,-3} //node 5 <br>
    };
    public static final String[] access_policy_threshold_example_2_rho = new String[] {
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
    };
    public static final String[] access_policy_threshold_example_2_satisfied01 = new String[] {
            "2", "3", "5", "6", "7", "9", "10",
    };
    public static final String[] access_policy_threshold_example_2_unsatisfied01 = new String[] {
            "1", "3", "4", "6", "7", "9", "10",
    };
    public static final String[] access_policy_threshold_example_2_unsatisfied02 = new String[] {
            "2", "3", "5", "6", "8", "9",
    };
}
