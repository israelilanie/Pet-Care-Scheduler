package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.user.Customer;
import com.udacity.jdnd.course3.critter.user.CustomerDTO;
import com.udacity.jdnd.course3.critter.user.CustomerRepository;
import com.udacity.jdnd.course3.critter.user.Employee;
import com.udacity.jdnd.course3.critter.user.EmployeeDTO;
import com.udacity.jdnd.course3.critter.user.EmployeeRepository;
import com.udacity.jdnd.course3.critter.user.EmployeeRequestDTO;
import com.udacity.jdnd.course3.critter.user.EmployeeSkill;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;

    public UserService(CustomerRepository customerRepository, EmployeeRepository employeeRepository) {
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        customer.setName(customerDTO.getName());
        customer.setPhoneNumber(customerDTO.getPhoneNumber());
        customer.setNotes(customerDTO.getNotes());
        return toCustomerDTO(customerRepository.save(customer));
    }

    @Transactional(readOnly = true)
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAllByOrderByIdAsc().stream()
                .map(this::toCustomerDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomerDTO getOwnerByPet(long petId) {
        return customerRepository.findByPetsId(petId)
                .map(this::toCustomerDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
    }

    @Transactional
    public EmployeeDTO saveEmployee(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        employee.setName(employeeDTO.getName());
        employee.setSkills(employeeDTO.getSkills() == null ? new HashSet<>() : new HashSet<>(employeeDTO.getSkills()));
        if (employeeDTO.getDaysAvailable() != null) {
            employee.setDaysAvailable(new HashSet<>(employeeDTO.getDaysAvailable()));
        }
        return toEmployeeDTO(employeeRepository.save(employee));
    }

    @Transactional(readOnly = true)
    public EmployeeDTO getEmployee(long employeeId) {
        return toEmployeeDTO(findEmployee(employeeId));
    }

    @Transactional
    public void setAvailability(Set<DayOfWeek> daysAvailable, long employeeId) {
        Employee employee = findEmployee(employeeId);
        employee.setDaysAvailable(daysAvailable == null ? new HashSet<>() : new HashSet<>(daysAvailable));
        employeeRepository.save(employee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeDTO> findEmployeesForService(EmployeeRequestDTO employeeDTO) {
        DayOfWeek requestedDay = employeeDTO.getDate().getDayOfWeek();
        Set<EmployeeSkill> requestedSkills = employeeDTO.getSkills() == null ? Set.of() : employeeDTO.getSkills();
        return employeeRepository.findAll().stream()
                .filter(employee -> employee.getDaysAvailable() != null && employee.getDaysAvailable().contains(requestedDay))
                .filter(employee -> employee.getSkills() != null && employee.getSkills().containsAll(requestedSkills))
                .map(this::toEmployeeDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Employee findEmployee(long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
    }

    private CustomerDTO toCustomerDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setNotes(customer.getNotes());
        List<Long> petIds = customer.getPets() == null ? new ArrayList<>() : customer.getPets().stream()
                .map(Pet::getId)
                .toList();
        dto.setPetIds(petIds);
        return dto;
    }

    private EmployeeDTO toEmployeeDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setSkills(employee.getSkills() == null ? new HashSet<>() : new HashSet<>(employee.getSkills()));
        if (employee.getDaysAvailable() != null && !employee.getDaysAvailable().isEmpty()) {
            dto.setDaysAvailable(new HashSet<>(employee.getDaysAvailable()));
        }
        return dto;
    }
}
