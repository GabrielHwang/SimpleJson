package com.gabriel.hson;

public interface TreeNode {
    int size();
    boolean isArray();
    boolean isObject();
    TreeNode get(int index);
    TreeNode get(String fieldName);
    TreeNode path(String fieldName);
    TreeNode path(int index);
}
