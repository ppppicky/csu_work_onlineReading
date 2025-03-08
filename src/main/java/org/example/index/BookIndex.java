package org.example.index;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;
import java.util.List;

@Document(indexName = "book_index")
@Data
public class BookIndex {
    @Id
    private Integer id;
    private String bookName;
    private String author;

    private List<String> keywords;

}
