package com.joe.robot.wx.dto;

import com.joe.utils.parse.xml.XmlIgnoreProperties;
import lombok.Data;

@Data
@XmlIgnoreProperties(ignoreUnknown = true)
public class WxProperties {
    private String skey;
    private String wxsid;
    private String wxuin;
    private String pass_ticket;
}
