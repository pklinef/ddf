/**
 * Copyright (c) Codice Foundation
 *
 *
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http:></http:>//www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.security.servlet.whoami

import com.google.gson.GsonBuilder
import ddf.security.SecurityConstants
import ddf.security.common.SecurityTokenHolder
import ddf.security.http.SessionFactory
import ddf.security.service.SecurityManager
import ddf.security.service.SecurityServiceException
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.*
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class WhoAmIServlet : HttpServlet() {

    private val LOGGER = LoggerFactory.getLogger(WhoAmIServlet::class.java)

    var httpSessionFactory: SessionFactory? = null

    var securityManager: SecurityManager? = null

    private val gson = GsonBuilder().serializeNulls().setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .serializeNulls()
            .create()

    @Throws(ServletException::class, IOException::class)
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.setHeader("Cache-Control", "no-cache, no-store")
        resp.setHeader("Pragma", "no-cache")

        val session = httpSessionFactory!!.getOrCreateSession(req)
        val tokenMap = (session.getAttribute(
                SecurityConstants.SAML_ASSERTION) as SecurityTokenHolder).realmTokenMap

        val realmToWhoMap = HashMap<String, WhoAmI>()
        for ((realm, token) in tokenMap) {
            try {
                val whoAmI = WhoAmI(securityManager!!.getSubject(token))
                realmToWhoMap.put(realm, whoAmI)
            } catch (e: SecurityServiceException) {
                LOGGER.debug("Unable to get subject from realm ($realm) token.", e)
            }

        }

        resp.contentType = "application/json"
        resp.writer.print(gson.toJson(realmToWhoMap))
    }

}
