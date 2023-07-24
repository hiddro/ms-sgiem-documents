package com.sgiem.ms.documents.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Document(collection = "files")
public class File {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    @NotNull
    @NotEmpty
    @Size(max = 100)
    private String titulo;

    @NotNull
    @NotEmpty
    @Size(max = 30)
    private String tipoFile;

    @NotNull
    @NotEmpty
    @Size(max = 30)
    private String code;

    @NotNull
    @NotEmpty
    @Size(max = 10)
    private String tipoDocumento;

    @NotNull
    @NotEmpty
    @Size(max = 100)
    private String url;
}
