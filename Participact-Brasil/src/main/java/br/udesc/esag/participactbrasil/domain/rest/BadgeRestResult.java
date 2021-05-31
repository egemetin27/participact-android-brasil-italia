package br.udesc.esag.participactbrasil.domain.rest;

public class BadgeRestResult implements Comparable<BadgeRestResult> {

    private long id;
    private String title;
    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {

        if (title == null)
            throw new NullPointerException();
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {

        if (description == null)
            throw new NullPointerException();

        this.description = description;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (((Object) this).getClass() != obj.getClass())
            return false;
        BadgeRestResult other = (BadgeRestResult) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        return true;
    }

    @Override
    public int compareTo(BadgeRestResult o) {
        if (o == null)
            return 1;
        if (equals(o))
            return 0;

        int compareTitle = title.compareTo(o.getTitle());

        if (compareTitle != 0)
            return compareTitle;

        return description.compareTo(o.getDescription());

    }


}
