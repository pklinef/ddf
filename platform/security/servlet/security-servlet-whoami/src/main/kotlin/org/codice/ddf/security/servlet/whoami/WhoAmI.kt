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

import ddf.security.SubjectUtils
import ddf.security.assertion.SecurityAssertion
import ddf.security.principal.GuestPrincipal
import org.apache.shiro.subject.Subject
import org.joda.time.DateTime
import org.joda.time.Period
import org.joda.time.format.PeriodFormatterBuilder
import org.opensaml.saml.saml2.core.AuthnStatement
import java.util.*

typealias Claims = Map<String, SortedSet<String>>

class WhoAmI(subject: Subject) {

    val name: String

    val displayName: String

    val email: String

    val notOnOrAfter: Date?

    val notBefore: Date?

    val expiresIn: String

    val issuer: String

    val isGuest: Boolean

    val authnContextClasses: List<String>

    val claims: Claims

    init {
        val assertion = extractSecurityAssertion(subject) ?:
                throw IllegalArgumentException("Missing security assertion in given subject.")

        name = SubjectUtils.getName(subject)
        displayName = SubjectUtils.getName(subject, null, true)

        email = SubjectUtils.getEmailAddress(subject) ?: UNKNOWN
        claims = SubjectUtils.getSubjectAttributes(subject)

        issuer = assertion.issuer
        isGuest = assertion.principal is GuestPrincipal

        notOnOrAfter = assertion.notOnOrAfter
        notBefore = assertion.notBefore
        expiresIn = durationUntilNow(notOnOrAfter)

        authnContextClasses = extractAuthnContextClasses(assertion.authnStatements)
    }

    private fun extractSecurityAssertion(subject: Subject?): SecurityAssertion? {
        return subject?.principals?.oneByType(SecurityAssertion::class.java)
    }

    private fun durationUntilNow(expiration: Date?): String {
        return expiration?.let {
            TIME_FORMATTER.print(Period(expiration.time - DateTime.now()
                    .millis).normalizedStandard())
        } ?: UNKNOWN
    }

    private fun extractAuthnContextClasses(authnStatements: List<AuthnStatement>): List<String> {
        return authnStatements.map { it.authnContext?.authnContextClassRef?.authnContextClassRef }
                .filterNotNull()
    }

    companion object {

        private val UNKNOWN = "Unknown"

        private val TIME_FORMATTER = PeriodFormatterBuilder()
                .appendDays()
                .appendSuffix(" day", " days")
                .appendSeparator(" ")
                .appendHours()
                .appendSuffix(" hour", " hours")
                .appendSeparator(" ")
                .appendMinutes()
                .appendSuffix(" minute", " minutes")
                .appendSeparator(" ")
                .appendSeconds()
                .appendSuffix(" second", " seconds")
                .toFormatter()
    }

}
