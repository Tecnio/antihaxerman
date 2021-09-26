package me.tecnio.antihaxerman.util.type;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class VpnInfo {

    String country = "N/A";
    Boolean isVpn = true;

    public VpnInfo(String country, boolean isVPN) {
        this.isVpn = isVPN;
        this.country = country;
    }
}
