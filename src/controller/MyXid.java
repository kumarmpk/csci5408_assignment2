package controller;

import javax.transaction.xa.Xid;

public class MyXid implements Xid{

    private int formatId;
    private byte globalTransactionId[];
    private byte branchQualifier[];

    public MyXid(){}

    public MyXid(int formatId, byte globalTransactionId[], byte branchQualifier[]){
        this.formatId = formatId;
        this.globalTransactionId = globalTransactionId;
        this.branchQualifier = branchQualifier;
    }

    @Override
    public int getFormatId() {
        return this.formatId;
    }

    @Override
    public byte[] getGlobalTransactionId() {
        return this.globalTransactionId;
    }

    @Override
    public byte[] getBranchQualifier() {
        return this.branchQualifier;
    }
}