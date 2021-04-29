package server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PatientModel {
    private int rowsOnPage;
    private int currentPage=1;
    ArrayList<Patient> patients;

    public PatientModel(int rowsOnPage) {
        patients = new ArrayList<Patient>();
        this.rowsOnPage = rowsOnPage;
    }

    public int getRowsOnPage() { return rowsOnPage; }

    public void setRowsOnPage(int rowsOnPage) { this.rowsOnPage = rowsOnPage; }

    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

    public int getCurrentPage() { return currentPage; }

    public List<Object> getPage(int page) {
        if (patients.size() / rowsOnPage + 1 < page) return null;
        if (page <= 0) return null;

        int startIdx = (page-1)*rowsOnPage;
        int endIdx = Math.min(page*rowsOnPage, patients.size());

        return patients.subList(startIdx, endIdx).stream()
                .map(Patient::toObject)
                .collect(Collectors.toList());
    }

    public ArrayList<Patient> getPatients() { return patients; }

    public void setPatients(ArrayList<Patient> patients) { this.patients = patients; }
}
