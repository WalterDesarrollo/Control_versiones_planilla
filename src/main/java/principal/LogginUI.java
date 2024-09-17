package principal;

import java.io.IOException;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import umg.edu.gt.DAO.ConsultaLogginDAO;
import umg.edu.gt.DTO.DatosLogginDTO;

/**
 *
 * @author X
 */
@ManagedBean(name="loginUI")
public class LogginUI implements Serializable {

    private DatosLogginDTO loginDTO = new DatosLogginDTO();
    private String mensaje;

    public DatosLogginDTO getLoginDTO() {
        return loginDTO;
    }

    public void setLoginDTO(DatosLogginDTO loginDTO) {
        this.loginDTO = loginDTO;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public void validarLogin() throws IOException {
        ConsultaLogginDAO loginDAO = new ConsultaLogginDAO();
        DatosLogginDTO usuario = loginDAO.validarLogin(loginDTO);
        if (usuario != null) {
            setMensaje("Bienvenido " + usuario.getUsername());
            FacesContext.getCurrentInstance().getExternalContext().redirect("MenuInicio.xhtml");
        } else {
            setMensaje("Usuario o contraseña incorrectos");
        }
    }
}