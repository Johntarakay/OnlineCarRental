package application.controllers;


import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import application.GUI.DriversLicenseForm;
import application.models.Car;
import application.models.Rental;
import application.repositories.CarRepository;
import application.services.CarService;
import application.services.RentalService;
import application.services.ShopService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

public class CarSelectionFormController implements Initializable {
	// Service classes
	private CarRepository carRepository;
	private AppContext context;
	private CarService carService;
	private ShopService shopService;
	private RentalService rentalService;

	
	/***
	 * The main window of the application. Consists of filters, 
	 * the main sheet for selecting a car and selecting additional services 
	 * (designed for coordination with the manager). The user first selects additional 
	 * information, then selects a car, then selects additional services.
	 * 
	 * @param context
	 * @param carService
	 * @param shopService
	 * @param rentalService
	 */
	public CarSelectionFormController(AppContext context) {
		System.out.println("THIS IS CONTEXT: "+context);
		this.context = context;
		this.carService = context.getCarService();
		this.shopService = context.getShopService();
		this.rentalService = context.getRentalService();
		this.carRepository = context.getCarRepository();
	}

	// Lists for Items
	private List<Car> researchedCarsByfirstBrand = new ArrayList<Car>();;
	private List<Car> researchedCarsBysecondBrand = new ArrayList<Car>();;
	private List<Car> researchedCarsBythirdBrand = new ArrayList<Car>();;
	private List<Car> allResearchedCarsByBrands = new ArrayList<Car>();
	private List<Car> allCars;
	private List<String> citiesList;
	private List<String> pointsAll;
	private List<String> cityPoints;
	private List<String> brands;

	// service object
	private String brand;

	// client information
	private String city;
	private String point;
	private LocalDate localDateFrom;
	private LocalDate localDateTo;
	private StringBuilder sb;
	
	//main frame (pane)
    @FXML 
    private Accordion accordion;
    @FXML 
    private TitledPane filtersPane;
    @FXML 
    private TitledPane carsPane;
    @FXML 
    private TitledPane extraPane;
    

	// filters
	@FXML
	private ChoiceBox<String> cities;

	@FXML
	private ChoiceBox<String> points;

	@FXML
	private DatePicker dateFrom; //!

	@FXML
	private Label dateFromLabel;

	@FXML
	private DatePicker dateTo;//!

	@FXML
	private Label dateToLabel;

	@FXML
	private ChoiceBox<String> drivingExperience;

	@FXML
	private ChoiceBox<String> brand1;

	@FXML
	private ChoiceBox<String> brand2;

	@FXML
	private ChoiceBox<String> brand3;

	@FXML
	private TextField minPrice;

	@FXML
	private Label minPriceLabel;

	@FXML
	private TextField maxPrice;

	@FXML
	private Label maxPriceLabel;

	// cars
	@FXML
	private ListView<Car> carsList;

	// extra services
	@FXML
	private TextField pickUpAddress;

	@FXML
	private CheckBox insurance;

	@FXML
	private CheckBox GPS;

	@FXML
	private CheckBox child;

	@FXML
	private CheckBox moreDriver;

	@FXML
	private CheckBox wifi;

	@FXML
	private CheckBox crossborder;

	@FXML
	private CheckBox tank;
	
	@FXML
	Label coleteInfoLabel;

	@FXML
	private Button complete;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("INIT WORKING");
		
		allCars = carService.listAvailableCars();
		System.out.println(allCars.toString());
		citiesList = shopService.getAllShopsCitiesList();
		System.out.println(citiesList);
		pointsAll = shopService.getAllShopsNames();
		System.out.println(pointsAll);
		brands = new ArrayList(carService.getCarBrands());
		System.out.println(brands);
		
		
	    if (carService == null) {
	        System.out.println("WARNING: carService is null — skipping UI load.");
	        return;
	    }
		//prepare accordion
		accordion.setExpandedPane(filtersPane);
		accordion.expandedPaneProperty().addListener((obs, oldPane, newPane) -> {
	        if (newPane != null) {
	            if (newPane == carsPane) {
	                String max = maxPrice.getText();
	                if(!max.isBlank()) {
	                	getClientMaxPrice();
	                }
	                String min = minPrice.getText();
	                if(!min.isBlank()) {
	                	getClientMinPrice();
	                }
	            }
	        }
	    });
		
		// prepare cities CBox
		ObservableList<String> citiesItems = FXCollections.observableArrayList(citiesList);
		cities.setItems(citiesItems);
		cities.setOnAction(this::getCitiesChoice);
		
		// prepare points CBox
		ObservableList<String> pointsItems = FXCollections.observableArrayList(pointsAll);
		points.setItems(pointsItems);
		points.setOnAction(this::getPointsChoice);
		
		// prepare drivingExperience (drivingYears) CBox
		ObservableList<String> driveYears = FXCollections
				.observableArrayList(Arrays.asList("more than 2 years", "more than 3 years"));
		drivingExperience.setItems(driveYears);
		drivingExperience.setOnAction(this::getDrivingYearsChoice);
		
		// prepare brands CBox
		List<String> brands1 = new ArrayList(brands);
		brands1.addFirst("All brands");
		List<String> brands2 = new ArrayList(brands);
		brands2.addFirst("None");
		ObservableList<String> brandFirstItems = FXCollections.observableArrayList(brands1);
		brand1.setItems(brandFirstItems);
		ObservableList<String> brandLastItems = FXCollections.observableArrayList(brands2);
		brand1.setItems(brandFirstItems);
		brand2.setItems(brandLastItems);
		brand3.setItems(brandLastItems);
		brand1.setOnAction(this::getFirstBrandsForSearch);
		brand2.setOnAction(this::getSecondBrandsForSearch);
		brand3.setOnAction(this::getThirdBrandForSearch);
		
		minPrice.setPromptText("Min price is " + carService.getMinPrice());
		maxPrice.setPromptText("Max price is " + carService.getMaxPrice());
		//prepare list of cars
		carsList.setItems(FXCollections.observableArrayList(allCars));
		//EXTRA
		sb = new StringBuilder();
		sb.append("Extra services: ");	
		
		dateFrom.setOnAction(this::getDateStartRental);
		dateTo.setOnAction(this::getDateEndRental);
		
		 complete.setOnAction(ev -> {
		        System.out.println("Click to complete!");
		        clickComplete(ev);
		    });
	}

	public void getCitiesChoice(ActionEvent e) {
		// TODO FOR GOOD TIME
		System.out.println("CITY. There is no software logic here now, but in the future this choice will affect "
				+ "\nthe list of cars, since each point of sale has its own list of cars "
				+ "\n(points on diferent cities).");
		city = cities.getValue();
		List<String> filteredShops = shopService.getShopNamesByCity(city);
		ObservableList<String> pointsNewItems = FXCollections.observableArrayList(filteredShops);
		points.setItems(pointsNewItems);
	}

	public void getPointsChoice(ActionEvent e) {
		// TODO FOR GOOD TIME
		System.out.println("POINT. There is no software logic here now, but in the future this choice will affect "
				+ "\nthe list of cars, since each point of sale has its own list of cars.");
		point = points.getValue();
		rentalService.getCurrentRental().setShop(point);
	}

	public void getDrivingYearsChoice(ActionEvent e) {
		// TODO FOR GOOD TIME
		System.out.println(
				"DRIVE EXSP. There is no software logic behind this at the moment, \nbut in the future this choice may affect the availability of certain classes of cars.");
	}

	public void getFirstBrandsForSearch(ActionEvent e) {
		if (!allResearchedCarsByBrands.isEmpty()) {
			if (!researchedCarsByfirstBrand.isEmpty()) {
				allResearchedCarsByBrands.removeAll(researchedCarsByfirstBrand);
			}
		}
		brand = brand1.getValue();
		if (brand.equalsIgnoreCase("All brands")) {
			allResearchedCarsByBrands = allCars;
			researchedCarsByfirstBrand = new ArrayList<Car>();
		}
		researchedCarsByfirstBrand = carRepository.getListCarByBrand(brand); 
		allResearchedCarsByBrands.addAll(researchedCarsByfirstBrand);
		carsList.setItems(FXCollections.observableArrayList(allResearchedCarsByBrands));
	}

	public void getSecondBrandsForSearch(ActionEvent e) {
		if (!allResearchedCarsByBrands.isEmpty()) {
			if (!researchedCarsBysecondBrand.isEmpty()) {
				if (brand1.getValue().equalsIgnoreCase("All brands")) {
					return;
				}
				allResearchedCarsByBrands.removeAll(researchedCarsBysecondBrand);
			}
		}
		brand = brand2.getValue();
		if (brand.equalsIgnoreCase("none")) {

		}
		researchedCarsBysecondBrand = carRepository.getListCarByBrand(brand);
		allResearchedCarsByBrands.addAll(researchedCarsBysecondBrand);
		carsList.setItems(FXCollections.observableArrayList(allResearchedCarsByBrands));
	}

	public void getThirdBrandForSearch(ActionEvent e) {
		if (!allResearchedCarsByBrands.isEmpty()) {
			if (!researchedCarsBythirdBrand.isEmpty()) {
				if (brand1.getValue().equalsIgnoreCase("All brands")) {
					return;
				}
				allResearchedCarsByBrands.removeAll(researchedCarsBythirdBrand);
			}
		}
		brand = brand3.getValue();
		researchedCarsBythirdBrand = carRepository.getListCarByBrand(brand);
		allResearchedCarsByBrands.addAll(researchedCarsBythirdBrand);
		carsList.setItems(FXCollections.observableArrayList(allResearchedCarsByBrands));
	}

	public void getDateStartRental(ActionEvent e) {
		localDateFrom = dateFrom.getValue();
	}

	public void getDateEndRental(ActionEvent e) {
		localDateTo = dateTo.getValue();
		try {
			System.out.println("Validation is not work now");
//			ValidationService.validateRentalDates(localDateFrom, localDateTo);
			System.out.println("Dates! "+localDateFrom+", "+localDateTo);
			rentalService.getCurrentRental().setRentalStartDate(localDateFrom);
			rentalService.getCurrentRental().setRentalEndDate(localDateTo);
		} catch (Exception exep) {
			String error = "Dates are not valid";
			System.out.println(error);
			dateFromLabel.setText(error);
			dateFromLabel.setStyle("-fx-text-fill: red;");
			dateToLabel.setText(error);
			dateToLabel.setStyle("-fx-text-fill: red;");
			return;
		}
		
	}
	
	public void clickComplete(ActionEvent e) {
		Car selectedCar = carsList.getSelectionModel().getSelectedItem();
		if (selectedCar != null) {
		    System.out.println("Selected car: " + selectedCar);
		    rentalService.getCurrentRental().setCar(selectedCar);
		} else {
		    System.out.println("No selected cars");
		    coleteInfoLabel.setText("Selected machine is missing");
		    return;
		}
		
		//extra
		if(insurance.isSelected()) {
			sb.append("full insurance; ");
		}
		if(GPS.isSelected()) {
			sb.append("GPS; ");
		}
		if(child.isSelected()) {
			sb.append("child seat; ");
		}
		if(moreDriver.isSelected()) {
			sb.append("second driver; ");
		}
		if(moreDriver.isSelected()) {
			sb.append("second driver; ");
		}
		if(wifi.isSelected()) {
			sb.append("wifi; ");
		}
		if(crossborder.isSelected()) {
			sb.append("cross border; ");
		}
		if(tank.isSelected()) {
			sb.append("cross border; ");
		}
		if(crossborder.isSelected()) {
			sb.append("full tank; ");
		}
		if(!pickUpAddress.getText().isBlank()) {
			sb.append(pickUpAddress.getText());
		}
		rentalService.getCurrentRental().setPickUpLocation(sb.toString());
		
		Rental rental = rentalService.getCurrentRental();
		System.out.println("This is RENTAL: "+rental.toString());
		if(rental.getRentalStartDate()!=null
				&&rental.getRentalEndDate()!=null
				&&rental.getCar()!=null
				&&rental.getPickUpLocation()!=null
				&&rental.getShop()!=null) {
			
			context.getRentalRepository().updateRental(rental);
			//change window
			Stage currentStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
			new DriversLicenseForm().startDriversLicenseForm(currentStage, context);
			
		}else {
			coleteInfoLabel.setText("The data or machine is missing");
			coleteInfoLabel.setStyle("-fx-text-fill: red;");
		    return;
		}
	}
	
	public void getClientMinPrice() {
		String value = minPrice.getText();
		try {
			double minPrice = Double.valueOf(value);
			List listCarByMinPrice = carRepository.getListCarByMinPrice(minPrice);
			allResearchedCarsByBrands.retainAll(listCarByMinPrice);
			carsList.setItems(FXCollections.observableArrayList(allResearchedCarsByBrands));
			
		} catch (Exception e) {
			String er = "Wrong price";
			System.out.println(er);
			minPriceLabel.setText(er);
		}
	}

	public void getClientMaxPrice() {
		String value = maxPrice.getText();
		try {
			double maxPrice = Double.valueOf(value.trim());
			List<Car> listCarByMaxPrice = carRepository.getListCarByMaxPrice(maxPrice);
			allResearchedCarsByBrands.retainAll(listCarByMaxPrice);
			carsList.setItems(FXCollections.observableArrayList(allResearchedCarsByBrands));
		} catch (Exception e) {
			String er = "Wrong price";
			System.out.println(er);
			maxPriceLabel.setText(er);
		}
	}
}

