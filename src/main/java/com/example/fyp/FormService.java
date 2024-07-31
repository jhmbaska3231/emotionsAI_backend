package com.example.fyp;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FormService {
 
    @Autowired
    private FormRepository formRepository;

    public Form saveForm(Form form) {
        form.setReadStatus(false);
        return formRepository.save(form);
    }

    public List<Form> getAllForms() {
        return formRepository.findAll();
    }

    public Optional<Form> updateReadStatus(int id, boolean readStatus) {
        Optional<Form> form = formRepository.findById(id);
        if (form.isPresent()) {
            Form updatedForm = form.get();
            updatedForm.setReadStatus(readStatus);
            formRepository.save(updatedForm);
            return Optional.of(updatedForm);
        } else {
            return Optional.empty();
        }
    }

}
