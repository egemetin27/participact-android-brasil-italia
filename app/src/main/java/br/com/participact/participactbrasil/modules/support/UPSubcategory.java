package br.com.participact.participactbrasil.modules.support;

public class UPSubcategory {

    private Long id;
    private String name;
    private UPCategory category;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UPCategory getCategory() {
        return category;
    }

    public void setCategory(UPCategory category) {
        this.category = category;
    }
}
