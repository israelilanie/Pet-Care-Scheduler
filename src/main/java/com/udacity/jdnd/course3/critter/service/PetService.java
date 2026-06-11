package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.pet.PetDTO;
import com.udacity.jdnd.course3.critter.pet.PetRepository;
import com.udacity.jdnd.course3.critter.user.Customer;
import com.udacity.jdnd.course3.critter.user.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final CustomerRepository customerRepository;

    public PetService(PetRepository petRepository, CustomerRepository customerRepository) {
        this.petRepository = petRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public PetDTO savePet(PetDTO petDTO) {
        Customer owner = customerRepository.findById(petDTO.getOwnerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found"));

        Pet pet = new Pet();
        pet.setName(petDTO.getName());
        pet.setType(petDTO.getType());
        pet.setBirthDate(petDTO.getBirthDate());
        pet.setNotes(petDTO.getNotes());
        pet.setOwner(owner);

        Pet savedPet = petRepository.save(pet);
        owner.getPets().add(savedPet);
        return toPetDTO(savedPet);
    }

    @Transactional(readOnly = true)
    public PetDTO getPet(long petId) {
        return toPetDTO(findPet(petId));
    }

    @Transactional(readOnly = true)
    public List<PetDTO> getPets() {
        return petRepository.findAllByOrderByIdAsc().stream()
                .map(this::toPetDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PetDTO> getPetsByOwner(long ownerId) {
        return petRepository.findByOwnerIdOrderByIdAsc(ownerId).stream()
                .map(this::toPetDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Pet findPet(long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found"));
    }

    private PetDTO toPetDTO(Pet pet) {
        PetDTO dto = new PetDTO();
        dto.setId(pet.getId());
        dto.setName(pet.getName());
        dto.setType(pet.getType());
        dto.setBirthDate(pet.getBirthDate());
        dto.setNotes(pet.getNotes());
        dto.setOwnerId(pet.getOwner().getId());
        return dto;
    }
}
