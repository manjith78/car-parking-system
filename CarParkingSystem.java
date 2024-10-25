import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;

class ParkingSlot {
    private int slotNumber;
    private String carNumber;
    private String ownerName;
    private String parkingType;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private double billAmount; // Field for the bill amount

    public ParkingSlot(int slotNumber, String carNumber, String ownerName, String parkingType, LocalDateTime checkInTime, double billAmount) {
        this.slotNumber = slotNumber;
        this.carNumber = carNumber;
        this.ownerName = ownerName;
        this.parkingType = parkingType;
        this.checkInTime = checkInTime;
        this.billAmount = billAmount; // Initialize the bill amount
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public boolean isOccupied() {
        return carNumber != null;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getParkingType() {
        return parkingType;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public double getBillAmount() {
        return billAmount; // Getter for the bill amount
    }

    public void setBillAmount(double billAmount) { // Setter for the bill amount
        this.billAmount = billAmount;
    }
}

public class CarParkingSystem {
    private HashMap<Integer, ParkingSlot> parkingSlots;
    private JFrame mainFrame;

    public CarParkingSystem() {
        parkingSlots = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            parkingSlots.put(i, new ParkingSlot(i, null, null, null, null, 0.0));
        }

        // Initialize the main frame
        mainFrame = new JFrame("Car Parking System");
        mainFrame.setSize(400, 300);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new FlowLayout());

        JButton guestButton = new JButton("Guest");
        guestButton.addActionListener(e -> guestMenu());
        mainFrame.add(guestButton);

        JButton managerButton = new JButton("Manager");
        managerButton.addActionListener(e -> managerLogin());
        mainFrame.add(managerButton);

        mainFrame.setVisible(true);
    }

    private void guestMenu() {
        JDialog guestDialog = new JDialog(mainFrame, "Guest Menu", true);
        guestDialog.setSize(400, 200);
        guestDialog.setLayout(new FlowLayout());

        JButton checkInButton = new JButton("Check In");
        checkInButton.addActionListener(e -> checkIn());
        guestDialog.add(checkInButton);

        JButton checkOutButton = new JButton("Check Out");
        checkOutButton.addActionListener(e -> checkOut());
        guestDialog.add(checkOutButton);

        guestDialog.setVisible(true);
    }

    private void managerLogin() {
        JDialog loginDialog = new JDialog(mainFrame, "Manager Login", true);
        loginDialog.setSize(300, 150);
        loginDialog.setLayout(new GridLayout(3, 2));

        loginDialog.add(new JLabel("Username:"));
        JTextField usernameField = new JTextField();
        loginDialog.add(usernameField);

        loginDialog.add(new JLabel("Password:"));
        JPasswordField passwordField = new JPasswordField();
        loginDialog.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (validateManager(username, password)) {
                loginDialog.dispose();
                managerMenu();
            } else {
                JOptionPane.showMessageDialog(loginDialog, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        loginDialog.add(loginButton);

        loginDialog.setVisible(true);
    }

    private void managerMenu() {
        JDialog managerDialog = new JDialog(mainFrame, "Manager Menu", true);
        managerDialog.setSize(400, 300);
        managerDialog.setLayout(new FlowLayout());

        JButton viewParkedCarsButton = new JButton("View Parked Cars");
        viewParkedCarsButton.addActionListener(e -> viewParkedCars());
        managerDialog.add(viewParkedCarsButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> managerDialog.dispose());
        managerDialog.add(logoutButton);

        managerDialog.setVisible(true);
    }

    private void checkIn() {
        JDialog checkInDialog = new JDialog(mainFrame, "Check-In", true);
        checkInDialog.setSize(400, 300);
        checkInDialog.setLayout(new GridLayout(4, 2));

        checkInDialog.add(new JLabel("Enter Slot Number:"));
        JTextField slotNumberField = new JTextField();
        checkInDialog.add(slotNumberField);

        checkInDialog.add(new JLabel("Car Number:"));
        JTextField carNumberField = new JTextField();
        checkInDialog.add(carNumberField);

        checkInDialog.add(new JLabel("Owner Name:"));
        JTextField ownerNameField = new JTextField();
        checkInDialog.add(ownerNameField);

        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(e -> {
            try {
                int slotNumber = Integer.parseInt(slotNumberField.getText());
                if (slotNumber < 1 || slotNumber > 10) {
                    JOptionPane.showMessageDialog(checkInDialog, "Please enter a valid slot number (1-10).", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (parkingSlots.get(slotNumber).isOccupied()) {
                    JOptionPane.showMessageDialog(checkInDialog, "Slot " + slotNumber + " is already occupied.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    String carNumber = carNumberField.getText();
                    String ownerName = ownerNameField.getText();

                    if (carNumber.isEmpty() || ownerName.isEmpty()) {
                        JOptionPane.showMessageDialog(checkInDialog, "Please enter car details.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        ParkingSlot slot = new ParkingSlot(slotNumber, carNumber, ownerName, "Standard", LocalDateTime.now(), 0.0);
                        parkingSlots.put(slotNumber, slot);
                        saveCheckInToDatabase(slotNumber, carNumber, ownerName);
                        JOptionPane.showMessageDialog(checkInDialog, "Check-in successful!");
                        checkInDialog.dispose();
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(checkInDialog, "Please enter a valid numeric slot number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        checkInDialog.add(confirmButton);
        checkInDialog.setVisible(true);
    }

    private void checkOut() {
        JDialog checkOutDialog = new JDialog(mainFrame, "Check-Out", true);
        checkOutDialog.setSize(400, 300);
        checkOutDialog.setLayout(new GridLayout(2, 2));

        checkOutDialog.add(new JLabel("Enter Slot Number:"));
        JTextField slotNumberField = new JTextField();
        checkOutDialog.add(slotNumberField);

        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(e -> {
            try {
                int slotNumber = Integer.parseInt(slotNumberField.getText());
                if (slotNumber < 1 || slotNumber > 10) {
                    JOptionPane.showMessageDialog(checkOutDialog, "Please enter a valid slot number (1-10).", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (!parkingSlots.get(slotNumber).isOccupied()) {
                    JOptionPane.showMessageDialog(checkOutDialog, "Slot " + slotNumber + " is not occupied.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    ParkingSlot slot = parkingSlots.get(slotNumber);
                    slot.setCheckOutTime(LocalDateTime.now());
                    double billAmount = calculateBill(slot.getCheckInTime(), slot.getCheckOutTime());
                    slot.setBillAmount(billAmount); // Set the bill amount
                    saveCheckOutToDatabase(slotNumber, slot.getCarNumber(), slot.getCheckInTime(), slot.getCheckOutTime(), billAmount);
                    JOptionPane.showMessageDialog(checkOutDialog, "Check-out successful!\nBill Amount: ₹" + billAmount);
                    checkOutDialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(checkOutDialog, "Please enter a valid numeric slot number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        checkOutDialog.add(confirmButton);
        checkOutDialog.setVisible(true);
    }

    private void viewParkedCars() {
        StringBuilder carsDetails = new StringBuilder("Currently Parked Cars:\n");
        for (ParkingSlot slot : parkingSlots.values()) {
            if (slot.isOccupied()) {
                carsDetails.append("Slot Number: ").append(slot.getSlotNumber())
                        .append(", Car Number: ").append(slot.getCarNumber())
                        .append(", Owner: ").append(slot.getOwnerName())
                        .append(", Check-In Time: ").append(slot.getCheckInTime())
                        .append(", Bill Amount: ₹").append(slot.getBillAmount())
                        .append("\n");
            }
        }
        JOptionPane.showMessageDialog(mainFrame, carsDetails.toString());
    }

    private double calculateBill(LocalDateTime checkInTime, LocalDateTime checkOutTime) {
        long minutes = java.time.Duration.between(checkInTime, checkOutTime).toMinutes();
        return minutes * 1.5; // Charge ₹1.5 per minute
    }

    private boolean validateManager(String username, String password) {
        return "admin".equals(username) && "password".equals(password); // Replace with your own logic for validating manager credentials
    }

    private void saveCheckInToDatabase(int slotNumber, String carNumber, String ownerName) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/cardb", "postgres", "root");
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO parking_records (slot_number, car_number, owner_name, check_in_time) VALUES (?, ?, ?, ?)")) {
            preparedStatement.setInt(1, slotNumber);
            preparedStatement.setString(2, carNumber);
            preparedStatement.setString(3, ownerName);
            preparedStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveCheckOutToDatabase(int slotNumber, String carNumber, LocalDateTime checkInTime, LocalDateTime checkOutTime, double billAmount) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/cardb", "postgres", "root");
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE parking_records SET check_out_time = ?, bill_amount = ? WHERE slot_number = ? AND car_number = ?")) {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(checkOutTime));
            preparedStatement.setDouble(2, billAmount);
            preparedStatement.setInt(3, slotNumber);
            preparedStatement.setString(4, carNumber);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CarParkingSystem::new);
    }
}
