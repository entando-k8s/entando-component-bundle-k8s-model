package org.entando.kubernetes.model.bundle;

public class EntandoComponentBundleAuthor {

    private String name;
    private String email;

    EntandoComponentBundleAuthor(String name, String email){
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
