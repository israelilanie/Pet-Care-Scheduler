package com.udacity.jdnd.course3.critter;

import com.udacity.jdnd.course3.critter.pet.PetDTO;
import com.udacity.jdnd.course3.critter.schedule.ScheduleDTO;
import com.udacity.jdnd.course3.critter.service.PetService;
import com.udacity.jdnd.course3.critter.service.ScheduleService;
import com.udacity.jdnd.course3.critter.service.UserService;
import com.udacity.jdnd.course3.critter.user.EmployeeDTO;
import com.udacity.jdnd.course3.critter.user.EmployeeRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(classes = CritterApplication.class)
class ServiceEdgeCaseTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PetService petService;

    @Autowired
    private ScheduleService scheduleService;

    @Test
    void missingResourcesReturnNotFound() {
        assertNotFound(() -> userService.getOwnerByPet(999L));
        assertNotFound(() -> userService.getEmployee(999L));
        assertNotFound(() -> petService.getPet(999L));

        PetDTO pet = new PetDTO();
        pet.setOwnerId(999L);
        assertNotFound(() -> petService.savePet(pet));
    }

    @Test
    void emptyScheduleCollectionsArePersistedAsEmptyCollections() {
        ScheduleDTO schedule = new ScheduleDTO();
        schedule.setDate(LocalDate.of(2026, 9, 3));

        ScheduleDTO saved = scheduleService.createSchedule(schedule);

        assertEquals(List.of(), saved.getEmployeeIds());
        assertEquals(List.of(), saved.getPetIds());
        assertEquals(0, saved.getActivities().size());
    }

    @Test
    void employeesWithoutAvailabilityDoNotMatchServiceRequest() {
        EmployeeDTO employee = new EmployeeDTO();
        employee.setName("Unavailable employee");
        userService.saveEmployee(employee);

        EmployeeRequestDTO request = new EmployeeRequestDTO();
        request.setDate(LocalDate.of(2026, 9, 3));

        assertEquals(List.of(), userService.findEmployeesForService(request));
    }

    private static void assertNotFound(ThrowingRunnable operation) {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, operation::run);
        assertEquals(404, exception.getStatusCode().value());
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run();
    }
}
