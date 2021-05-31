package br.udesc.esag.participactbrasil.domain.rest;

public class ScoreRestResult implements Comparable<ScoreRestResult> {

    private String userName;
    private long userId;
    private int scoreValue;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {

        if (userName == null)
            throw new NullPointerException();

        this.userName = userName;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(int scoreValue) {
        this.scoreValue = scoreValue;
    }

    @Override
    public int compareTo(ScoreRestResult o) {
        if (o == null)
            return 1;
        ScoreRestResult score = (ScoreRestResult) o;
        if (equals(o))
            return 0;
        if (scoreValue > o.scoreValue)
            return 1;
        else if (scoreValue < o.scoreValue)
            return -1;
        else {
            return userName.compareTo(score.userName);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + scoreValue;
        result = prime * result + (int) (userId ^ (userId >>> 32));
        result = prime * result
                + ((userName == null) ? 0 : userName.hashCode());
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
        ScoreRestResult other = (ScoreRestResult) obj;
        if (scoreValue != other.scoreValue)
            return false;
        if (userId != other.userId)
            return false;
        if (userName == null) {
            if (other.userName != null)
                return false;
        } else if (!userName.equals(other.userName))
            return false;
        return true;
    }

}
