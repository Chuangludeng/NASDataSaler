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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javax.swing.*;


public class MainWindowController implements Initializable{
    @FXML
    private Button mNewWalletButton;
    @FXML
    private Button mImportWalletButton;
    @FXML
    private Button mExportWalletButton;

    @FXML
    private Label mWalletAddressLabel;
    @FXML
    private Label mWalletBalanceLabel;

    @FXML
    private TextField myTextField;

    NASManager mNASManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        mWalletAddressLabel.setText("");
        mWalletBalanceLabel.setText("");
        mNASManager = NASManager.getInstance();
    }

    public void NewWalletButton()
    {
        String inputValue = JOptionPane.showInputDialog("请设置你的钱包密码");

        if(inputValue != "") {
            mNASManager.CreateAccount(inputValue);
            mWalletAddressLabel.setText(mNASManager.GetAccountAddress());
            mWalletBalanceLabel.setText("0");
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
                mNASManager.ImportWallet(data.getBytes(),inputValue.getBytes());
                mWalletAddressLabel.setText(mNASManager.GetAccountAddress());
                mWalletBalanceLabel.setText(mNASManager.GetAccountBalance());
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



}
