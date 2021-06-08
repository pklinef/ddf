/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package ddf.catalog.transformer.common.tika.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PagedContentHandler extends DefaultHandler {

  private static final String DIV_TAG = "div";

  private final List<String> pages = new ArrayList<>();

  private final int writeLimit;

  private StringBuffer page;

  private boolean inDivTag = false;

  public PagedContentHandler(int pageWriteLimit) {
    writeLimit = pageWriteLimit;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts)
      throws SAXException {
    if (DIV_TAG.equalsIgnoreCase(localName)) {
      inDivTag = true;
      page = new StringBuffer();
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (DIV_TAG.equalsIgnoreCase(localName)) {
      inDivTag = false;
      String pageText = page.toString();

      if (pageText.length() == 0) {
        return;
      }

      if (writeLimit != -1 && pageText.length() > writeLimit) {
        pages.add(pageText.substring(0, writeLimit));
      } else {
        pages.add(pageText);
      }
    }
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    if (inDivTag) {
      page.append(ch);
    }
  }

  @Override
  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    characters(ch, start, length);
  }

  public List<String> getPages() {
    return Collections.unmodifiableList(pages);
  }
}
