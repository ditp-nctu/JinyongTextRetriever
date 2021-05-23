/*
 * Copyright 2021 Jonathan Chang, Chun-yien <ccy@musicapoetica.org>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package c1632.cctcc.art.jytr;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import static org.apache.jena.rdf.model.ResourceFactory.*;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

/**
 *
 * @author Jonathan Chang, Chun-yien <ccy@musicapoetica.org>
 */
@AllArgsConstructor
@Getter
@ToString
public class Chapter {

  public static final String JY = "http://myjinyongontology.info/";
  public static final String SCHEMA = "https://schema.org/";

  public static final Property schema_name = createProperty(SCHEMA, "name");
  public static final Property schema_isPartOf = createProperty(SCHEMA, "isPartOf");
  public static final Property schema_position = createProperty(SCHEMA, "position");
  public static final Resource schema_chapter = createResource(SCHEMA + "Chapter");

  private Resource res_book;
  private String book_id;    // 書名鍵值
  private String number;  // 章回數
  private String title;   // 章回標題
  private String url;     // 全文連結

  public int getOrder() {

    try {
      return Integer.valueOf(new URL(url).getFile().replace(".htm", "").split("/")[2]);
    } catch (MalformedURLException ex) {
      Logger.getLogger(Chapter.class.getName()).log(Level.SEVERE, null, ex);
    }
    return -1;
  }

  public List<Statement> getTriples() {

    var subject = createResource(JY + book_id + "_ch_" + getOrder());
    return List.of(createStatement(subject, RDF.type, schema_chapter),
            createStatement(subject, schema_isPartOf, res_book),
            createStatement(subject, RDFS.label, createLangLiteral(title, "zh")),
            createStatement(subject, schema_name, createLangLiteral(number + ": " + title, "zh")),
            createStatement(subject, schema_position, createTypedLiteral(getOrder())),
            createStatement(subject, OWL2.sameAs, createPlainLiteral(getUrl()))
    );
  }
}
