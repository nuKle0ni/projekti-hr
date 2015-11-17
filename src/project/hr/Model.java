/* Preliminary header information: 
 * Model.java - Acts as the model in MVC (Model View Controler) design pattern.
 * Samuli Siitonen
 *
 * Main logic for class interaction is based on an article by Robert Eckstein: 
 * "Java SE Application Design With MVC", March 2007
 * http://www.oracle.com/technetwork/articles/javase/index-142890.html
 */

//http://stackoverflow.com/questions/15758685/how-to-write-logs-in-text-file-when-using-java-util-logging-logger

package project.hr;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Samuli
 */
public class Model {
    private PropertyChangeSupport propertyChangeSupport;
    private PasswordSecurity passwordSecurity;
    private DatabaseHandler databaseHandler;
    private Employee signedInEmployee;
    
    private ArrayList<Employee> employeeSearchResults;
    
    public Model () {
        propertyChangeSupport = new PropertyChangeSupport(this);
        employeeSearchResults = new ArrayList();
        passwordSecurity = new PasswordSecurity();
        signedInEmployee = null;
        
        try {
            databaseHandler = new DatabaseHandler();
            databaseHandler.connect();
        }
        catch(Exception e) {
            // Log error, send quit/error event
        }
    }

    public Employee getSignedInEmployee() {
        return signedInEmployee;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener 
            propertyChangeListener) {
        propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener 
            propertyChangeListener) {
        propertyChangeSupport.removePropertyChangeListener(
                propertyChangeListener);
    }
    
    private void fireModelActionResult(String propertyName, Object oldValue,
            Object newValue) {
        
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, 
                newValue);
        
    }
    
    public void registerEmployee(Employee employee, String password) {
        try {
            employee.setPassword(
                    passwordSecurity.generateHashedSaltedPassword(password));
            databaseHandler.insertEmployee(employee);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        
        System.out.println("Registered new employee:");
        System.out.println("\tUsername:\t " + employee.getEmail());
        System.out.println("\t<hash>.<salt>:\t " + employee.getPassword());
    }
        
  
    private Employee searchEmployeeByEmail(String emailAddress) {
        Employee employee = null;
        
        try {
            employee = databaseHandler.selectEmployeeByEmailAddress(
                            emailAddress);
        } 
        catch(Exception ex) {
            // Write loh
            ex.printStackTrace();
        }
   
        return employee;
    }
    
    public void signIn(String emailAddress, String password) {
        Employee employee = searchEmployeeByEmail(emailAddress);
        System.out.println("model signin hash "+employee.getPassword());
        if(employee != null && passwordSecurity.isPasswordValid(password, 
                        employee.getPassword())) {
        
            signedInEmployee = employee;
            fireModelActionResult("sign_in", null, employee);
        }
        else {
            System.out.println("Model false");
            fireModelActionResult("sign_in", null, null);
        }
    }
    
    public void signOut() {
        fireModelActionResult("sign_out", signedInEmployee, null);
        signedInEmployee = null;
    }
    
    public void addEmployee(Employee employee) {
        boolean isSuccessfull = true;
       
        try {
            employee.setPassword(
                    passwordSecurity.generateHashedSaltedPassword(
                            employee.getPassword()));
            databaseHandler.insertEmployee(employee);
    
        } catch (Exception ex) {
            ex.printStackTrace();
            isSuccessfull = false;
            // Write log
        }
        
        fireModelActionResult("add", null, isSuccessfull);
    }
    
    public void editEmployee(Employee employee) {
        // Perform databse update, check thrown exceptions
    
        //fireModelActionResult("edit", null, (Object)employee);
    }
    
    public void removeEmployee(String socialSecurityNumber) {
        // Perform database delete, check thrown exceptions
        
        //fireModelActionResult("delete", null, true/false);
    }
    
    // Should store search results in case the user wants to alter them
    // (calls alterEmployeeSearchResultFormatting on an instance variable)
    public void searchEmployee(Employee employee) {
        try {
            employeeSearchResults = databaseHandler.selectEmployee(employee);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        fireModelActionResult("format_employee_search", null, 
                employeeSearchResults);
    }
    
    // http://stackoverflow.com/questions/18441846/how-to-sort-an-arraylist-in-java
    public void getFormattedEmployeeSearchResults(/*Formatting options*/) {
        // Alter returned database search results and send them forward with event propagation
        // (employeeSearchResults)
        Comparator comparator = null;
        
        if(false) {
            comparator = new Comparator<Employee>() {
                @Override
                public int compare(Employee firstEmployee, 
                        Employee secondEmployee) {
                    return firstEmployee.getFirstName().compareTo(
                            secondEmployee.getFirstName());
                }
            };  
        }
        else if(false) {
            comparator = new Comparator<Employee>() {
                @Override
                public int compare(Employee firstEmployee, 
                        Employee secondEmployee) {
                    return firstEmployee.getFirstName().compareTo(
                            secondEmployee.getFirstName());
                }
            };
        }
        
        if(comparator != null) {
            Collections.sort(employeeSearchResults, comparator);   
            fireModelActionResult("format_employee_search_results", null, 
                    employeeSearchResults);
        }
        else {
            fireModelActionResult("format_employee_search_results", null, null);
        }
    }
    
    public void getAllEmployees() {
        ArrayList<Employee> employeeList = null;
        
        try {
            employeeList = databaseHandler.selectAllEmployees();
        } catch(Exception e) {
            e.printStackTrace();
            // Write log
        }

        fireModelActionResult("all_employees", null, employeeList);
    }
}
