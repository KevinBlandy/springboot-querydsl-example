package io.springboot.example.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@With

@Entity
@Table(name = "department", uniqueConstraints = {
	@UniqueConstraint(columnNames = "title", name = "title")
})
@org.hibernate.annotations.Table(appliesTo = "department", comment = "部门")
public class Department {
	
	@Id
	@Column(columnDefinition = "INT UNSIGNED COMMENT 'ID'")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(columnDefinition = "VARCHAR(50) COMMENT '名称'", nullable = false)
	private String title;
	
	@Column(columnDefinition = "VARCHAR(200) COMMENT '备注'")
	private String remark;
	
	@Column(columnDefinition = "TINYINT UNSIGNED COMMENT '是否启用。0：禁用，1：启用'", nullable = false)
	private Boolean enabled;
	
	@Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'", nullable = false)
	private LocalDateTime createAt;
}
