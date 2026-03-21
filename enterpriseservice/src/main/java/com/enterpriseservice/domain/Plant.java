package com.enterpriseservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "plant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Plant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	@NotBlank
	private String plantName;

	private String location;
	private String latitude;
	private String longitude;
	private String status;
	private BigDecimal capacityPct;
	private Integer totalLines;
	private Integer linesActive;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "plant_supplier",
			joinColumns = @JoinColumn(name = "plant_id"),
			inverseJoinColumns = @JoinColumn(name = "supplier_id"),
			uniqueConstraints = @UniqueConstraint(name = "uk_plant_supplier", columnNames = {"plant_id", "supplier_id"})
	)
	@Builder.Default
	private Set<Supplier> suppliers = new HashSet<>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "plant_shipment",
			joinColumns = @JoinColumn(name = "plant_id"),
			inverseJoinColumns = @JoinColumn(name = "shipment_id"),
			uniqueConstraints = @UniqueConstraint(name = "uk_plant_shipment", columnNames = {"plant_id", "shipment_id"})
	)
	@Builder.Default
	@JsonIgnoreProperties({"plants", "suppliers"})
	private Set<Shipment> plantShipments = new HashSet<>();
}
