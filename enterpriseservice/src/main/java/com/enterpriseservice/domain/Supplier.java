package com.enterpriseservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "supplier")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Supplier {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	@NotBlank
	private String supplierName;

	private String location;
	private String latitude;
	private String longitude;
	private String contractStatus;

	@ManyToMany(mappedBy = "suppliers", fetch = FetchType.LAZY)
	@Builder.Default
	@JsonIgnoreProperties({"suppliers", "plantShipments"})
	private Set<Plant> plants = new HashSet<>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "supplier_shipment",
			joinColumns = @JoinColumn(name = "supplier_id"),
			inverseJoinColumns = @JoinColumn(name = "shipment_id"),
			uniqueConstraints = @UniqueConstraint(name = "uk_supplier_shipment", columnNames = {"supplier_id", "shipment_id"})
	)
	@Builder.Default
	@JsonIgnoreProperties({"plants", "suppliers"})
	private Set<Shipment> shipments = new HashSet<>();
}
