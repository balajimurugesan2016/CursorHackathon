package com.enterpriseservice.service;

import com.enterpriseservice.domain.Plant;
import com.enterpriseservice.domain.Shipment;
import com.enterpriseservice.domain.Supplier;
import com.enterpriseservice.repository.PlantRepo;
import com.enterpriseservice.repository.ShipmentRepo;
import com.enterpriseservice.repository.SupplierRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MasterDataService {

	private final PlantRepo plantRepo;
	private final SupplierRepo supplierRepo;
	private final ShipmentRepo shipmentRepo;

	// --- Plant ---

	@Transactional(readOnly = true)
	public List<Plant> listPlants() {
		return plantRepo.findAll();
	}

	@Transactional(readOnly = true)
	public Plant getPlant(Long id) {
		return plantRepo.findDetailById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant not found"));
	}

	@Transactional
	public Plant createPlant(Plant plant) {
		plant.setId(null);
		stripIncomingAssociations(plant);
		return plantRepo.save(plant);
	}

	@Transactional
	public Plant updatePlant(Long id, Plant patch) {
		Plant existing = plantRepo.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant not found"));
		if (patch.getPlantName() != null) existing.setPlantName(patch.getPlantName());
		if (patch.getLocation() != null) existing.setLocation(patch.getLocation());
		if (patch.getLatitude() != null) existing.setLatitude(patch.getLatitude());
		if (patch.getLongitude() != null) existing.setLongitude(patch.getLongitude());
		if (patch.getStatus() != null) existing.setStatus(patch.getStatus());
		if (patch.getCapacityPct() != null) existing.setCapacityPct(patch.getCapacityPct());
		if (patch.getTotalLines() != null) existing.setTotalLines(patch.getTotalLines());
		if (patch.getLinesActive() != null) existing.setLinesActive(patch.getLinesActive());
		return plantRepo.save(existing);
	}

	@Transactional
	public void deletePlant(Long id) {
		Plant plant = plantRepo.findDetailById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant not found"));
		for (Supplier s : plant.getSuppliers()) {
			s.getPlants().remove(plant);
		}
		plant.getSuppliers().clear();
		for (Shipment sh : plant.getPlantShipments()) {
			sh.getPlants().remove(plant);
		}
		plant.getPlantShipments().clear();
		plantRepo.delete(plant);
	}

	// --- Supplier ---

	@Transactional(readOnly = true)
	public List<Supplier> listSuppliers() {
		return supplierRepo.findAll();
	}

	@Transactional(readOnly = true)
	public Supplier getSupplier(Long id) {
		return supplierRepo.findDetailById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found"));
	}

	@Transactional
	public Supplier createSupplier(Supplier supplier) {
		supplier.setId(null);
		stripIncomingAssociations(supplier);
		return supplierRepo.save(supplier);
	}

	@Transactional
	public Supplier updateSupplier(Long id, Supplier patch) {
		Supplier existing = supplierRepo.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found"));
		if (patch.getSupplierName() != null) existing.setSupplierName(patch.getSupplierName());
		if (patch.getLocation() != null) existing.setLocation(patch.getLocation());
		if (patch.getLatitude() != null) existing.setLatitude(patch.getLatitude());
		if (patch.getLongitude() != null) existing.setLongitude(patch.getLongitude());
		if (patch.getContractStatus() != null) existing.setContractStatus(patch.getContractStatus());
		return supplierRepo.save(existing);
	}

	@Transactional
	public void deleteSupplier(Long id) {
		Supplier supplier = supplierRepo.findDetailById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found"));
		for (Plant p : supplier.getPlants()) {
			p.getSuppliers().remove(supplier);
		}
		supplier.getPlants().clear();
		for (Shipment sh : supplier.getShipments()) {
			sh.getSuppliers().remove(supplier);
		}
		supplier.getShipments().clear();
		supplierRepo.delete(supplier);
	}

	// --- Shipment ---

	@Transactional(readOnly = true)
	public List<Shipment> listShipments() {
		return shipmentRepo.findAll();
	}

	@Transactional(readOnly = true)
	public Shipment getShipment(Long id) {
		return shipmentRepo.findDetailById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipment not found"));
	}

	@Transactional
	public Shipment createShipment(Shipment shipment) {
		shipment.setId(null);
		stripIncomingAssociations(shipment);
		return shipmentRepo.save(shipment);
	}

	@Transactional
	public Shipment updateShipment(Long id, Shipment patch) {
		Shipment existing = shipmentRepo.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipment not found"));
		if (patch.getShipmentItem() != null) existing.setShipmentItem(patch.getShipmentItem());
		if (patch.getQuantity() != null) existing.setQuantity(patch.getQuantity());
		if (patch.getShipNumber() != null) existing.setShipNumber(patch.getShipNumber());
		if (patch.getStatus() != null) existing.setStatus(patch.getStatus());
		if (patch.getReceiveDate() != null) existing.setReceiveDate(patch.getReceiveDate());
		return shipmentRepo.save(existing);
	}

	@Transactional
	public void deleteShipment(Long id) {
		Shipment shipment = shipmentRepo.findDetailById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipment not found"));
		for (Supplier s : shipment.getSuppliers()) {
			s.getShipments().remove(shipment);
		}
		shipment.getSuppliers().clear();
		for (Plant p : shipment.getPlants()) {
			p.getPlantShipments().remove(shipment);
		}
		shipment.getPlants().clear();
		shipmentRepo.delete(shipment);
	}

	// --- Links (M:N) ---

	@Transactional
	public void linkPlantSupplier(Long plantId, Long supplierId) {
		Plant plant = plantRepo.findById(plantId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant not found"));
		Supplier supplier = supplierRepo.findById(supplierId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found"));
		plant.getSuppliers().add(supplier);
		supplier.getPlants().add(plant);
	}

	@Transactional
	public void unlinkPlantSupplier(Long plantId, Long supplierId) {
		Plant plant = plantRepo.findById(plantId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant not found"));
		Supplier supplier = supplierRepo.findById(supplierId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found"));
		plant.getSuppliers().remove(supplier);
		supplier.getPlants().remove(plant);
	}

	@Transactional
	public void linkSupplierShipment(Long supplierId, Long shipmentId) {
		Supplier supplier = supplierRepo.findById(supplierId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found"));
		Shipment shipment = shipmentRepo.findById(shipmentId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipment not found"));
		supplier.getShipments().add(shipment);
		shipment.getSuppliers().add(supplier);
	}

	@Transactional
	public void unlinkSupplierShipment(Long supplierId, Long shipmentId) {
		Supplier supplier = supplierRepo.findById(supplierId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found"));
		Shipment shipment = shipmentRepo.findById(shipmentId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipment not found"));
		supplier.getShipments().remove(shipment);
		shipment.getSuppliers().remove(supplier);
	}

	@Transactional
	public void linkPlantShipment(Long plantId, Long shipmentId) {
		Plant plant = plantRepo.findById(plantId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant not found"));
		Shipment shipment = shipmentRepo.findById(shipmentId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipment not found"));
		plant.getPlantShipments().add(shipment);
		shipment.getPlants().add(plant);
	}

	@Transactional
	public void unlinkPlantShipment(Long plantId, Long shipmentId) {
		Plant plant = plantRepo.findById(plantId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant not found"));
		Shipment shipment = shipmentRepo.findById(shipmentId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipment not found"));
		plant.getPlantShipments().remove(shipment);
		shipment.getPlants().remove(plant);
	}

	/** Ignore client-supplied M:N collections on create (links use {@code /api/v1/links}). */
	private static void stripIncomingAssociations(Plant plant) {
		plant.getSuppliers().clear();
		plant.getPlantShipments().clear();
	}

	private static void stripIncomingAssociations(Supplier supplier) {
		supplier.getPlants().clear();
		supplier.getShipments().clear();
	}

	private static void stripIncomingAssociations(Shipment shipment) {
		shipment.getSuppliers().clear();
		shipment.getPlants().clear();
	}
}
