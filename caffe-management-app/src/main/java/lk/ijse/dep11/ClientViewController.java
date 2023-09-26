package lk.ijse.dep11;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import lk.ijse.dep11.tb.Orders;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ClientViewController {
    public AnchorPane rootOrder;
    public TextField txtName;
    public TextField txtId;
    public TextField txtContact;
    public Button btnNewOrder;
    public TableView<Orders> tblOrderDetails;
    public Button btnPlaceOrder;
    public Spinner<Integer> spnBigKing;
    public Spinner<Integer> spnSupremeChicken;
    public Spinner<Integer> spnTexasSmoke;
    public Spinner<Integer> spnWhopperBeef;
    private ObjectOutputStream oos;
    //private ArrayList<Orders> orderList = new ArrayList<>();


    public void initialize(){

        spnBigKing.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,50,0,1));
        spnSupremeChicken.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,50,0,1));
        spnTexasSmoke.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,50,0,1));
        spnWhopperBeef.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,50,0,1));

        tblOrderDetails.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblOrderDetails.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblOrderDetails.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("contact"));
        tblOrderDetails.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("bk"));
        tblOrderDetails.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("se"));
        tblOrderDetails.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("tc"));
        tblOrderDetails.getColumns().get(6).setCellValueFactory(new PropertyValueFactory<>("wb"));
        tblOrderDetails.getColumns().get(7).setCellValueFactory(new PropertyValueFactory<>("status"));

        new Thread(this::startServer).start();
//        orderList = readEmployeeList();
//        ObservableList<Employee> observableEmployeeList = FXCollections.observableList(employeeList);
//        tblEmployee.setItems(observableEmployeeList);
        try{
            Socket remoteSocket = new Socket("192.168.1.144", 5050);
            OutputStream os = remoteSocket.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os);
            oos = new ObjectOutputStream(bos);
            System.out.println("Connected....!");

        } catch (IOException e){
            throw new RuntimeException(e);
        }

    }
    private void startServer(){
        try{
            ServerSocket serverSocket = new ServerSocket(6060);
            while(true){
                System.out.println("waiting for client connection");
                Socket localSocket = serverSocket.accept();
                System.out.println("Client Connected: "+localSocket);
                new Thread(()->{
                    try {
                        InputStream is = localSocket.getInputStream();
                        BufferedInputStream bis = new BufferedInputStream(is);
                        ObjectInputStream ois = new ObjectInputStream(bis);
                        while (true) {
                            Orders order = (Orders) ois.readObject();
                            Platform.runLater(() -> tblOrderDetails.getItems().add(order));
                        }
                    }catch (EOFException e){

                    }catch (IOException e){
                        e.printStackTrace();
                    }catch(ClassNotFoundException e){
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void btnNewOrderOnAction(){
        txtId.clear();
        txtName.clear();
        txtContact.clear();
        txtId.requestFocus();

    }
    public void btnPlaceOrderOnAction(){
        if(!txtId.getText().strip().matches("C\\d{3}")){
            txtId.requestFocus();
            txtId.selectAll();
            return;
        } else if (!txtName.getText().strip().matches("[A-Za-z ]+")) {
            txtName.requestFocus();
            txtName.selectAll();
            return;

        }else if(txtContact.getText().isBlank() || txtContact.getText().strip().length()<3){
            txtContact.requestFocus();
            txtContact.selectAll();
            return;
        }
        var order = new Orders(txtId.getText(), txtName.getText(), txtContact.getText(),
                spnBigKing.getValue().toString(), spnSupremeChicken.getValue().toString(),
                spnTexasSmoke.getValue().toString(), spnWhopperBeef.getValue().toString(), "Pending");
        try{
            oos.writeObject(order);
            oos.flush();
            spnTexasSmoke.getValueFactory().setValue(0);
            spnWhopperBeef.getValueFactory().setValue(0);
            spnBigKing.getValueFactory().setValue(0);
            spnSupremeChicken.getValueFactory().setValue(0);
            txtId.clear();
            txtName.clear();
            txtContact.clear();
            txtId.requestFocus();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
