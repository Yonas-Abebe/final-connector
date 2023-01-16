package com.mycompany;

import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;

@Xml(prefix = "caas")
@Extension(name = "CAAS")
@Configurations(CustomConfiguration.class)
public class CustomExtension {

}
