package application.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import application.GUI.CarSelectionForm;
import application.models.Rental;
import application.services.RentalService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class RentalSummaryFormController implements Initializable {
	private AppContext context;
	private RentalService rentalService;
	private Rental currentRental;

	@FXML
	private Label infoLabel;
	@FXML
	private TextArea carInfoArea;
	@FXML
	private TextArea extraInfoArea;
	@FXML
	private TextArea rentalTextArea;
	@FXML
	private Button backButton;
	@FXML
	private Button newButton;
	@FXML
	private Button saveButton;
	@FXML
	private TextArea saveInfoArea;
	

	public RentalSummaryFormController(AppContext context) {
		this.context = context;
		this.rentalService = context.getRentalService();
		this.currentRental = rentalService.getCurrentRental();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		String carInfo = new String();
		if (currentRental.getCar() != null) {
		    carInfo = currentRental.getCar().toString();
		} else {
			carInfo = "No car selected";
		}

		carInfoArea.setText(carInfo);
		
		String extraInfo = new String();
		if (currentRental.getPickUpLocation() != null) {
		    extraInfo = currentRental.getPickUpLocation();
		} else {
			extraInfo = "No extra selected";
		}
		extraInfoArea.setText(extraInfo);
		
		StringBuilder sb = new StringBuilder();
		try{
		sb.append("Rental information:\nName: " + currentRental.getClient().getName());
		sb.append("\nPhone: " + currentRental.getClient().getPhone());
		sb.append("\nEmail: " + currentRental.getClient().getEmail());
		sb.append("\nLocation(shop point): " + currentRental.getShop());
		}catch(Exception e) {
			System.out.println("There ais no data");
			sb.append("There ais no data");
		}

		String rentalInfo = sb.toString();
		rentalTextArea.setText(rentalInfo);

		backButton.setOnAction(ev -> {
			System.out.println("Click to back!");
			clickBack(ev);
		});

		newButton.setOnAction(ev -> {
			System.out.println("Click to new!");
			clickNewRental(ev);
		});

		saveButton.setOnAction(ev -> {
			System.out.println("Click to save!");
			clickSave(ev);
		});
	}

	public void clickBack(ActionEvent e) {
		// change window
		Stage currentStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		new CarSelectionForm().startCarSelectionForm(currentStage, context);
	}

	public void clickNewRental(ActionEvent e) {
		// change window
		Stage currentStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		new CarSelectionForm().startCarSelectionForm(currentStage, context);
	}

	public void clickSave(ActionEvent e) {
		saveInfoArea.setText("SAVE INFORMATION:\nlogin: "+ currentRental.getClient().getEmail()+"\npassword: "+currentRental.getId());
	}
}
