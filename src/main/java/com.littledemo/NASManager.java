package com.littledemo;

import com.littledemo.Util.HttpClientUtil;
import io.nebulas.account.AccountManager;
import io.nebulas.core.Address;
import io.nebulas.core.Transaction;
import io.nebulas.core.TransactionCallPayload;
import io.nebulas.util.ByteUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NASManager {
    private static NASManager ourInstance = new NASManager();

    public static NASManager getInstance() {
        return ourInstance;
    }

    private String NAS_URL = "https://testnet.nebulas.io";
    private String NAS_GetAccountState = "/v1/user/accountstate";
    private String NAS_Call = "/v1/user/call";
    private String NAS_SendRawTransaction = "/v1/user/rawtransaction";
    private int chainID = 1001;//1 mainet,1001 testnet, 100 default private

    private AccountManager mAccountManager;
    private HttpClientUtil mHttpClientUtil;

    private Address mCurAddress = null;
    private int mCurNonce = 0;
    private byte[] mPassphrase;
    private String mPassphraseString;

    private NASManager() {
        try {
            mAccountManager = new AccountManager();
            mHttpClientUtil = new HttpClientUtil();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void CreateAccount(String passphrase)
    {
        try {
            mCurAddress = mAccountManager.newAccount(passphrase.getBytes());
            mPassphrase = passphrase.getBytes();
            mPassphraseString = passphrase;
            mCurNonce = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String GetAccountAddress()
    {
        if(mCurAddress != null)
            return mCurAddress.string();
        else
            return "";
    }

    public String GetAccountBalance()
    {
        if(mCurAddress != null)
        {
            JSONObject requestJson = new JSONObject();
            requestJson.put("address",mCurAddress.string());

            String responseJson = mHttpClientUtil.executeByPOST(NAS_URL+NAS_GetAccountState,requestJson.toString());

            JSONObject jObject = new JSONObject(responseJson);

            mCurNonce = jObject.getJSONObject("result").getInt("nonce");

            return jObject.getJSONObject("result").getString("balance");
        }
        else
            return "ERROR";
    }

    public byte[] ExportWallet() throws Exception {
        if(mCurAddress != null)
        {
            return mAccountManager.export(mCurAddress,mPassphrase);
        }
        else
        {
            return null;
        }
    }

    public void ImportWallet(byte[] keydata,String passphrase) throws Exception {
        mCurAddress = mAccountManager.load(keydata,passphrase.getBytes());
        mPassphrase = passphrase.getBytes();
        mPassphraseString = passphrase;
    }

    public JSONObject Call(String address,String function,String arg)
    {
        if(mCurAddress != null)
        {
            JSONObject requestJson = new JSONObject();
            requestJson.put("from",mCurAddress.string());
            requestJson.put("to",address);
            requestJson.put("value","0");
            requestJson.put("nonce",mCurNonce+1);
            requestJson.put("gasPrice","1000000");
            requestJson.put("gasLimit","2000000");
            JSONObject contractJson = new JSONObject();
            contractJson.put("function",function);
            contractJson.put("args",arg);
            requestJson.put("contract",contractJson);

            String responseJson = mHttpClientUtil.executeByPOST(NAS_URL+NAS_Call,requestJson.toString());

            JSONObject jObject = new JSONObject(responseJson);

            return jObject.getJSONObject("result");
        }
        else
            return null;
    }

    public String CallContractFunction(String address,int value,String function,String arg) throws Exception {
        if(mCurAddress != null) {

            Transaction.PayloadType payloadType = Transaction.PayloadType.CALL;
            byte[] payload = new TransactionCallPayload(function, arg).toBytes();
            // call to = contract address
            Address to = Address.ParseFromString(address);
            BigInteger gasPrice = new BigInteger("1000000"); // 0 < gasPrice < 10^12
            BigInteger gasLimit = new BigInteger("200000"); // 20000 < gasPrice < 50*10^9
            Transaction tx = new Transaction(chainID, mCurAddress, to, new BigInteger(String.valueOf(value)), mCurNonce+1, payloadType, payload, gasPrice, gasLimit);
            mAccountManager.signTransaction(tx,mPassphrase);
            byte[] rawData = tx.toProto();
            String rawDataString = ByteUtils.Base64ToString(rawData);
            JSONObject requestJson = new JSONObject();
            requestJson.put("data", rawDataString);

            String responseJson = mHttpClientUtil.executeByPOST(NAS_URL + NAS_SendRawTransaction, requestJson.toString());

            JSONObject jObject = new JSONObject(responseJson);
            return jObject.getJSONObject("result").getString("txhash");
        }
        else
            return "";
    }
}
