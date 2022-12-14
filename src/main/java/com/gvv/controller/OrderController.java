package com.gvv.controller;

import com.gvv.GVVApplication;
import com.gvv.entity.CustomerVO;
import com.gvv.entity.VehicleVO;
import com.gvv.service.impl.VOServiceImpl;
import com.gvv.utils.PdfUtils;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

@FXMLController
public class OrderController implements Initializable {

    private VehicleVO vehicleVO;
    private CustomerVO customerVO;

    private VOServiceImpl voServiceImpl;

    @FXML
    private Label CustomerInfoField;

    @FXML
    private Label VehicleInfoField;

    @FXML
    private VBox vBox;

    @FXML
    private RadioButton creditRBtn;

    @FXML
    private RadioButton cashRBtn;

    @FXML
    private Button payBtn;

    @FXML
    private Button cancelBtn;


    public void cancel() {
        Stage s = (Stage) cancelBtn.getScene().getWindow();
        s.close();
    }

    public void pay() {
        String paymentType = null;
        if (creditRBtn.isSelected()) {
            paymentType = "0";
        } else if (cashRBtn.isSelected()) {
            paymentType = "1";
        }
        BigDecimal salePrice = voServiceImpl.getVehiclePriceWithTax(vehicleVO.getPrice(), customerVO.getTaxRate(), vehicleVO.getPromotion());
        voServiceImpl.createOrder(customerVO.getCustomerId(), vehicleVO.getVehicleId(), paymentType, salePrice);
        File directory = new File("");
        GVVApplication.infoBox("Order successfully placed.\nThe certificate is located at: " + directory.getAbsolutePath().toString(), null, "Success");
        exportPdf();
        cancel();
    }

    public String getDate(Timestamp t) {
        StringBuilder sb = new StringBuilder();
        sb.append(t.getDate()).append('-').append(1 + t.getMonth()).append('-').append(1900 + t.getYear());
        return sb.toString();
    }

    public void exportPdf() {
        Map<String, String> map = new HashMap<String, String>();
        Map<String, Object> o = new HashMap<>();
        String date = (getDate(new Timestamp(System.currentTimeMillis())));
        map.put("nameSeller", "GvvApplication");
        map.put("nameBuyer", customerVO.getFirstName() + " " + customerVO.getLastName());
        map.put("dateSelle", date);
        map.put("price", voServiceImpl.getVehiclePriceWithTax(vehicleVO.getPrice(), customerVO.getTaxRate(), vehicleVO.getPromotion()).stripTrailingZeros().toPlainString());
        if (creditRBtn.isSelected()) {
            map.put("paymentType", "By credit");
        } else if (cashRBtn.isSelected()) {
            map.put("paymentType", "By Cash");
        }
        map.put("signSeller", "GvvApplication");
        map.put("dateSelle2", date);
        o.put("map", map);
        PdfUtils.pdfOut(o);
    }

    public void showCustomerInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(customerVO.getFirstName()).append(" ").append(customerVO.getLastName()).append("\n");
        sb.append(customerVO.getPhoneNumber()).append("\n");
        sb.append(customerVO.getEmail()).append("\n");
        sb.append(customerVO.getAddress());
        CustomerInfoField.setText(sb.toString());
    }

    public void showPayType() {
        ToggleGroup toggleGroup = new ToggleGroup();
        creditRBtn.setToggleGroup(toggleGroup);
        cashRBtn.setToggleGroup(toggleGroup);
    }

    public void showVehicleInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(vehicleVO.getInfo()).append("\n");
        sb.append("Tax rate: ").append(this.customerVO.getTaxRate().toPlainString()).append("\n");
        sb.append("Price with tax: ").append(voServiceImpl.getVehiclePriceWithTax(vehicleVO.getPrice(), customerVO.getTaxRate(), vehicleVO.getPromotion()).stripTrailingZeros().toPlainString());
        VehicleInfoField.setText(sb.toString());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.voServiceImpl = GVVApplication.voServiceImpl;
        this.vehicleVO = GVVApplication.vehicleVO;
        this.customerVO = GVVApplication.customerVO;
        showCustomerInfo();
        showPayType();
        showVehicleInfo();
    }
}
