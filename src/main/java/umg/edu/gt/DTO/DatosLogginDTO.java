/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package umg.edu.gt.DTO;

import java.io.Serializable;

public class DatosLogginDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;

    // Constructor por defecto
    public DatosLogginDTO() {
    }

    // Constructor con parámetros
    public DatosLogginDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters y setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Método toString() para facilitar la depuración
    @Override
    public String toString() {
        return "DatosLogginDTO{" +
               "username='" + username + '\'' +
               ", password='[PROTECTED]'" +
               '}';
    }
}