package ru.emelkrist.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "binary_content")
public class BinaryContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private byte[] fileAsArrayOfBytes;
}
