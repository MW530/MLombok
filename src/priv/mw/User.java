package priv.mw;

import priv.mw.annotations.AllArgsConstructor;
import priv.mw.annotations.Getters;
import priv.mw.annotations.Setters;
import priv.mw.annotations.ToString;

@ToString
@Getters
@Setters
@AllArgsConstructor
public class User {
    private Integer id;
    private String name;
    private String content;
}
