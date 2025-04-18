package application.repositories;

import models.Rental;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.util.stream.Collectors;

/**
 * The RentalRepository class provides CRUD operations for managing Rental records.
 * It maintains an in-memory list of Rental objects and supports operations
 * such as addition, retrieval, update, and deletion. In addition, it includes
 * methods to serialize and deserialize the rental list to and from a file.
 */
public class RentalRepository implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // The list that stores all Rental objects.
    private List<Rental> rentalList = new ArrayList<>();

    /**
     * Saves a new Rental record to the repository.
     *
     * @param rental the Rental object to be added.
     */
    public void save(Rental rental) {
        rentalList.add(rental);
    }

    /**
     * Retrieves a Rental record by its unique identifier.
     *
     * @param id the unique identifier of the Rental.
     * @return the Rental object matching the given id, or null if not found.
     */
    public Rental getRentalById(int id) {
        for (Rental rental : rentalList) {
            if (rental.getId() == id) {
                return rental;
            }
        }
        return null;
    }

    /**
     * Updates an existing Rental record in the repository.
     *
     * @param updatedRental the Rental object containing updated data.
     */
    public void updateRental(Rental updatedRental) {
        for (int i = 0; i < rentalList.size(); i++) {
            if (rentalList.get(i).getId() == updatedRental.getId()) {
                rentalList.set(i, updatedRental);
                break;
            }
        }
    }

    /**
     * Deletes a Rental record from the repository using its unique identifier.
     *
     * @param id the unique identifier of the Rental to be deleted.
     */
    public void deleteRental(int id) {
        rentalList.removeIf(rental -> rental.getId() == id);
    }

    /**
     * Saves the current rental list to a file using serialization.
     *
     * @param filename the path and name of the file to which rental data will be saved.
     */
    public void saveToFile(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(rentalList);
            System.out.println("Rental data successfully saved to file: " + filename);
        } catch (IOException e) {
            System.err.println("Error saving rental data: " + e.getMessage());
        }
    }

    /**
     * Loads the rental list from a file using deserialization.
     *
     * @param filename the path and name of the file from which rental data will be loaded.
     */
    @SuppressWarnings("unchecked")
    public void loadFromFile(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            rentalList = (List<Rental>) ois.readObject();
            System.out.println("Rental data successfully loaded from file: " + filename);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading rental data: " + e.getMessage());
        }
    }

    /**
     * Retrieves the complete list of Rental records stored in the repository.
     *
     * @return a List containing all Rental objects.
     */
    public List<Rental> getAllRentals() {
        return rentalList;
    }
}