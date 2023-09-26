package lk.ijse.dep11.tb;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Orders implements Serializable {
    private String id;
    private String name;
    private String contact;
    private String bk;
    private String se;
    private String tc;
    private String wb;
    private String status;


}
