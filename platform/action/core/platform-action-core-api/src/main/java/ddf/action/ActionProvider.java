/**
 * Copyright (c) Codice Foundation
 * <p/>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package ddf.action;

import java.util.Collections;
import java.util.List;

/**
 * This class provides {@link Action}s for a given subject. Objects that the
 * {@link ActionProvider} can handle are not restricted to a particular class and can be whatever
 * the {@link ActionProvider} is able to handle. <br>
 *
 * @see Action
 * @see ActionRegistry
 */
public interface ActionProvider {

    /**
     *
     * @param subject
     *            object for which the {@link ActionProvider} is requested to provide an
     *            {@link Action}
     * @return an {@link Action} object. If no action can be taken on the input, then
     *         <code>null</code> shall be returned
     *
     * @deprecated replaced by {@link #getActions(T subject)}
     */
    @Deprecated <T> Action getAction(T subject);

    /**
     * @param subject object for which the {@link ActionProvider} is requested to provide an
     *                {@link Action}
     * @return an {@link Action} object. If no action can be taken on the input, then
     * <code>Collections.emptyList()</code> shall be returned
     */
    default <T> List<Action> getActions(T subject) {
        return Collections.singletonList(getAction(subject));
    }

    /**
     * @return a unique identifier to distinguish the type of service this {@link ActionProvider}
     * provides
     */
    String getId();

    /**
     * Checks if an {@link ActionProvider} supports a given subject.
     * @param subject the input to check
     * @return true if is supported, false otherwise.
     */
    default <T> boolean canHandle(T subject) {
        return true;
    }

}