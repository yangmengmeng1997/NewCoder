package com.DFSExercise;

import java.util.HashMap;
import java.util.Map;

public class BuildTree {
    static Map<Integer, Integer> indexMap;
    public static void main(String []args){
        int[] preorder={3,9,20,15,7};
        int[] inorder={9,3,15,20,7};
        TreeNode root=buildTree(preorder,inorder);
        System.out.println("ok");
    }
        public static TreeNode myBuildTree(int[] preorder, int[] inorder, int preorder_left, int preorder_right, int inorder_left, int inorder_right) {
            if (preorder_left > preorder_right) {
                return null;
            }

            // 前序遍历中的第一个节点就是根节点,找到节点下标，再通过hash表映射找到中序遍历中的根节点
            int preorder_root = preorder_left;
            // 在中序遍历中定位根节点的下标
            int inorder_root = indexMap.get(preorder[preorder_root]);

            // 先把根节点建立出来
            TreeNode root = new TreeNode(preorder[preorder_root]);
            // 得到左子树中的节点数目
            int size_left_subtree = inorder_root - inorder_left;
            // 递归地构造左子树，并连接到根节点
            // 先序遍历中「从 左边界+1 开始的 size_left_subtree」个元素就对应了中序遍历中「从 左边界 开始到 根节点定位-1」的元素
            root.left = myBuildTree(preorder, inorder, preorder_left + 1, preorder_left + size_left_subtree, inorder_left, inorder_root - 1);
            // 递归地构造右子树，并连接到根节点
            // 先序遍历中「从 左边界+1+左子树节点数目 开始到 右边界」的元素就对应了中序遍历中「从 根节点定位+1 到 右边界」的元素
            root.right = myBuildTree(preorder, inorder, preorder_left + size_left_subtree + 1, preorder_right, inorder_root + 1, inorder_right);
            return root;
        }

        public static TreeNode buildTree(int[] preorder, int[] inorder) {
            int n = preorder.length;
            // 构造哈希映射，帮助我们快速定位根节点
            indexMap = new HashMap<Integer, Integer>();
            for (int i = 0; i < n; i++) {
                indexMap.put(inorder[i], i);
            }
            //return myBuildTree(preorder, inorder, 0, n - 1, 0, n - 1);
            return BuildTreeProcess(preorder,inorder,0,n-1,0,n-1);
        }
        public static TreeNode BuildTreeProcess(int[] preorder,int[] inorder,int pre_left,int pre_right,int in_left,int in_right){
            if(pre_left>pre_right)
                return null;
            int pre_root=pre_left;
            int inorder_root=indexMap.get(preorder[pre_root]);
            TreeNode root=new TreeNode(preorder[pre_root]);
            int size_leftchild=inorder_root-in_left;
            root.left=BuildTreeProcess(preorder,inorder,pre_left+1,pre_left+size_leftchild,in_left,inorder_root-1);
            root.right=BuildTreeProcess(preorder,inorder,pre_left+size_leftchild+1,pre_right,inorder_root+1,in_right);
            return root;
        }
    }