package com.example.application.views.list;

import com.example.application.data.entity.Contact;
import com.example.application.data.services.CrmService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.util.Collections;



@PageTitle("Contacts | Vaadin CRM")
@Route(value = "", layout = MainLayout.class)
@PermitAll
public class ListView extends VerticalLayout {
    private  CrmService service;
    Grid<Contact> grid = new Grid<>(Contact.class);
    TextField filterText = new TextField();
    ContactForm form;


    public ListView(CrmService service) {
        this.service = service;

        addClassName("list-view");
        setSizeFull();

        configureGrid();
        configureForm();
        

        add(
                getToolbar(),
                getContent()
        );
        updateList();
        closeEditor();

    }

    private void closeEditor() {
        form.setContact(null);
        form.setVisible(false);
        removeClassName("editing");




    }

    private void updateList() {
        grid.setItems(service.findAllContacts(filterText.getValue()));
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1,form);
        content.addClassName("content");
        content.setSizeFull();

        return content;
    }

    private void configureForm() {
        form =new ContactForm(service.findAllCompanies(), service.findAllStatuses());
        form.setWidth("25em");
        form.addListener(ContactForm.SaveEvent.class, this::saveContact);
        form.addListener(ContactForm.DeleteEvent.class, this::deleteContact);
        form.addListener(ContactForm.CloseEvent.class, e ->closeEditor());
    }

    private void deleteContact(ContactForm.DeleteEvent event) {
        service.deleteContact(event.getContact());
        updateList();
        closeEditor();
    }

    private void saveContact(ContactForm.SaveEvent event) {
        service.saveContact(event.getContact());
        updateList();
        closeEditor();

    }


    private Component getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY); // kullanıcı arama çubuğuna sürekli harfler girdiğinde, bu mod aktif olduğunda sistem bir süre bekler (genellikle birkaç saniye ya da kullanıcı yazmayı durdurduktan sonra) ve daha sonra olayı tetikler. Bu şekilde, kullanıcının girişi tamamlandıktan sonra bir kez sunucuya istek yapılır. Bu, gereksiz sunucu isteklerini azaltarak uygulamanın genel performansını artırır.
        filterText.addValueChangeListener(e -> updateList());

        Button addContactButton = new Button("Add Contact");
        addContactButton.addClickListener(e -> addContact());
        

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addContactButton);
        toolbar.addClassName("toolbar");

        return toolbar;

    }

    private void addContact() {
        grid.asSingleSelect().clear();
        editContact(new Contact());
    }

    private void configureGrid() {
        grid.addClassName("contact-grid");
        grid.setSizeFull();
        grid.setColumns("firstName", "lastName", "email");
        grid.addColumn(contact-> contact.getStatus().getName()).setHeader("Status");
        grid.addColumn(contact-> contact.getCompany().getName()).setHeader("Company");
        grid.getColumns().forEach(col->col.setAutoWidth(true));


        grid.asSingleSelect().addValueChangeListener(e -> editContact(e.getValue()));

    }

    private void editContact(Contact contact) {
        if (contact == null){
            closeEditor();
        } else {
            form.setContact(contact);
            form.setVisible(true);
            addClassName("editing");

        }


    }

}
