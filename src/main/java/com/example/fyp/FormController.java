package com.example.fyp;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/forms")
public class FormController {

    @Autowired
    private FormService formService;

    @PostMapping
    public ResponseEntity<Form> submitForm(@RequestBody Form form) {
        Form savedForm = formService.saveForm(form);
        return ResponseEntity.ok(savedForm);
    }

    @GetMapping
    public ResponseEntity<List<Form>> getAllForms() {
        List<Form> forms = formService.getAllForms();
        return ResponseEntity.ok(forms);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Form> updateReadStatus(@PathVariable int id, @RequestBody Map<String, Boolean> readStatus) {
        Optional<Form> updatedForm = formService.updateReadStatus(id, readStatus.get("readStatus"));
        return updatedForm.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    
}
