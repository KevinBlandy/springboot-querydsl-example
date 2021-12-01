package io.springboot.example.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
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
@Table(name = "user", uniqueConstraints = {
	@UniqueConstraint(columnNames = "name", name = "name")
}, indexes = {
	@Index(columnList = "department_id", name = "department_id")
})
@org.hibernate.annotations.Table(appliesTo = "user", comment = "用户")
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1691873956126863400L;
	
	@Id
	@Column(columnDefinition = "INT UNSIGNED COMMENT 'ID'")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(columnDefinition = "VARCHAR(50) COMMENT '名字'", nullable = false)
	private String name;
	
	@Column(columnDefinition = "VARCHAR(10) COMMENT '性别'", nullable = false)
	@Enumerated(EnumType.STRING)
	private Gender gender;
	
	@Column(name = "department_id", columnDefinition = "INT UNSIGNED COMMENT '部门ID'", nullable = false)
	private Integer departmentId;
	
	@Column(columnDefinition = "TINYINT UNSIGNED COMMENT '是否启用。0：禁用，1：启用'", nullable = false)
	private Boolean enabled;
	
	@Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'", nullable = false)
	private LocalDateTime createAt;
	
	public static enum Gender {
		MALE,		// 男
		FEMALE	// 女
	}
}
