package com.littledemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.json.JSONObject;

import javax.swing.*;


public class MainWindowController implements Initializable{
    @FXML
    private Button mNewWalletButton;
    @FXML
    private Button mImportWalletButton;
    @FXML
    private Button mExportWalletButton;

    @FXML
    private Button mCheckInfoPrice;

    @FXML
    private Button mPayInfo;

    @FXML
    private Button mReadInfo;

    @FXML
    private Button mCreatePayInfo;

    @FXML
    private Button mCheckBalance;

    @FXML
    private Button mWithdrawal;

    @FXML
    private Label mWalletAddressLabel;
    @FXML
    private Label mWalletBalanceLabel;

    @FXML
    private Label mBalanceLabel;

    @FXML
    private Label mInfoLabel;

    @FXML
    private TextField mCheckInfoAddress;

    @FXML
    private TextField mInputInfoPrice;

    @FXML
    private TextField mbuyInfoTX;

    @FXML
    private Label mInfoPrice;

    @FXML
    private TextArea mInfoText;

    @FXML
    private TextArea mInputInfoText;

    @FXML
    private TextArea mCreatInfoResult;

    @FXML
    private TabPane mMainPlane;

    NASManager mNASManager;

    private String Contract_address = "n21e3mr2v3E7psmaMDqnFKzVPsBMx5HdwYD";

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        mWalletAddressLabel.setText("");
        mWalletBalanceLabel.setText("");
        mNASManager = NASManager.getInstance();
        mMainPlane.setVisible(false);
        mInfoLabel.setVisible(true);
    }

    public void NewWalletButton()
    {
        String inputValue = JOptionPane.showInputDialog("请设置你的钱包密码");

        if(inputValue != "") {
            mNASManager.CreateAccount(inputValue);
            mWalletAddressLabel.setText(mNASManager.GetAccountAddress());
            mWalletBalanceLabel.setText("0");
            mMainPlane.setVisible(true);
            mInfoLabel.setVisible(false);
        }
    }

    public void ImportWalletButton()
    {
        JFileChooser jfc=new JFileChooser();
        if(jfc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
            File file=jfc.getSelectedFile();
            Scanner input= null;
            try {
                input = new Scanner(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String data = new String();
            while(input.hasNext()){
                data +=input.nextLine();
            }
            input.close();

            String inputValue = JOptionPane.showInputDialog("请输入你的钱包密码");

            try {
                mNASManager.ImportWallet(data.getBytes(),inputValue);
                mWalletAddressLabel.setText(mNASManager.GetAccountAddress());
                mWalletBalanceLabel.setText(String.valueOf(mNASManager.GetAccountBalance()));
                mMainPlane.setVisible(true);
                mInfoLabel.setVisible(false);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "导入失败", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        else
            System.out.println("No file is selected!");
    }

    public void ExportWalletButton()
    {
        JFileChooser jfc=new JFileChooser();
        if(jfc.showSaveDialog(null)==JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile();
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                out.write(mNASManager.ExportWallet());
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "文件保存失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
            catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "导出失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
        else {
            System.out.println("No file is selected!");
        }
    }

    public void CheckInfoPrice()
    {
        JSONObject jObject = mNASManager.Call(Contract_address,"priceOf",String.format("[\"%s\"]",mCheckInfoAddress.getText()));

        double newPrice = Double.valueOf(jObject.getString("result").replace("\"","")) / mNASManager.Ewei;

        String strPrice = "价格:"+ String.valueOf(newPrice) +"NAS";

        mInfoPrice.setText(strPrice);

        mWalletBalanceLabel.setText(String.valueOf(mNASManager.GetAccountBalance()));
    }

    public void CreatePayInfo()
    {
        String hash = null;
        try {
            long price = Math.round(Double.valueOf(mInputInfoPrice.getText())* mNASManager.Ewei);
            hash = mNASManager.CallContractFunction(Contract_address,0,"createInfo",String.format("[\"%s\",%d]",mInputInfoText.getText(),price));
        } catch (Exception e) {
            e.printStackTrace();
            mCreatInfoResult.setText(String.format("交易失败",hash));
        }

        mWalletBalanceLabel.setText(String.valueOf(mNASManager.GetAccountBalance()));

        mCreatInfoResult.setText(String.format("交易hash:%s",hash));
    }

    public void PayInfo()
    {
        JSONObject jObject = mNASManager.Call(Contract_address,"priceOf",String.format("[\"%s\"]",mCheckInfoAddress.getText()));
        long price = Long.valueOf(jObject.getString("result").replace("\"",""));

        mWalletBalanceLabel.setText(String.valueOf(mNASManager.GetAccountBalance()));

        String hash = null;
        try {
            hash = mNASManager.CallContractFunction(Contract_address,price,"buyInfo",String.format("[\"%s\"]",mCheckInfoAddress.getText()));
            mbuyInfoTX.setText(String.format("交易hash:%s",hash));
        } catch (Exception e) {
            e.printStackTrace();
            mbuyInfoTX.setText(String.format("交易失败",hash));
        }

        mWalletBalanceLabel.setText(String.valueOf(mNASManager.GetAccountBalance()));

    }

    public void ReadInfo()
    {
        JSONObject jObject2 = mNASManager.Call(Contract_address,"readInfo",String.format("[\"%s\"]",mCheckInfoAddress.getText()));

        String info = jObject2.getString("result");

        mInfoText.setText(info);

        mWalletBalanceLabel.setText(String.valueOf(mNASManager.GetAccountBalance()));
    }

    public void BalanceInfo()
    {
        JSONObject jObject2 = mNASManager.Call(Contract_address,"balanceOf","[]");

        double newPrice = Double.valueOf(jObject2.getString("result").replace("\"","")) / mNASManager.Ewei;

        mBalanceLabel.setText("余额:"+ String.valueOf(newPrice) +"NAS");

        mWalletBalanceLabel.setText(String.valueOf(mNASManager.GetAccountBalance()));
    }

    public void Withdrawal()
    {
        String hash = null;
        try {
            hash = mNASManager.CallContractFunction(Contract_address,0,"takeout","[]");
            mbuyInfoTX.setText(String.format("交易hash:%s",hash));
        } catch (Exception e) {
            e.printStackTrace();
            mbuyInfoTX.setText(String.format("交易失败",hash));
        }

        mWalletBalanceLabel.setText(String.valueOf(mNASManager.GetAccountBalance()));
    }

    public void UpdateWallet()
    {
        mWalletBalanceLabel.setText(String.valueOf(mNASManager.GetAccountBalance()));
    }
}
