package com.DFSExercise;

public class TotalNQueens {
    static int sum=0;
    public static void main(String[] args){
        solveNQueens(1);
        System.out.println(sum);
    }
    public static int solveNQueens(int n) {
        char [][]chess=new char[n][n];
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<n;j++){
                chess[i][j]='.';
            }
        }
        backtracking(n,chess,0);
        return sum;
    }
    public static void backtracking(int n,char [][]chess,int row){
        if(row==n){
            sum++;
            return;
        }
        //遍历每一列，因为我们是按行存放
        for(int col=0;col<n;col++){
            if(isValid(row,col,chess,n)){
                chess[row][col]='Q';
                backtracking(n,chess,row+1);
                chess[row][col]='.';
            }
        }
    }
    public static boolean isValid(int row,int col,char[][] chess,int n){
        //检查一列是否冲突，因为放皇后时按照每一行的顺序存放，所以只需要检查到
        //当前行及其斜上方是否冲突即可
        //1.检查当前行之上的同一列中是否有冲突
        for(int i=0;i<row;i++){
            if(chess[i][col]=='Q') return false;
            else continue;
        }
        //2.检查左上角对角线是否有冲突
        for(int i=row-1,j=col-1;i>=0 && j>=0;i--,j--){
            if(chess[i][j]=='Q') return false;
            else continue;
        }
        //检查右上方对角线是否有冲突，因为是从0开始，最多就是n-1
        for(int i=row-1,j=col+1;i>=0 && j<n;i--,j++){
            if(chess[i][j]=='Q') return false;
            else continue;
        }
        //都通过了返回false
        return true;
    }
}