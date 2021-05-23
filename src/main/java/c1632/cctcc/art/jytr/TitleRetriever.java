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

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFWriter;
import org.apache.jena.riot.RIOT;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDFS;
import org.jsoup.Jsoup;

/**
 *
 * @author Jonathan Chang, Chun-yien <ccy@musicapoetica.org>
 */
public class TitleRetriever {

  static final String template = "https://fpsother.tripod.com/%s/";
  static final Model model = ModelFactory.createDefaultModel();
  static final Resource BOOK = model.createResource(Chapter.SCHEMA + "Book");

  public static void main(String[] args) throws IOException {

    var works = Map.of(
            "倚天屠龍記", "Yi_Tian",
            "神雕俠侶", "Shen_Diao",
            "射雕英雄傳", "She_Diao"
    );

    model.setNsPrefix("", "http://myjinyongontology.info/");

    for (Entry<String, String> entry : works.entrySet()) {
      System.out.println("Processing " + entry.getKey());

      var chapters = extract(entry);
      chapters.stream()
              .peek(System.out::println)
              .map(Chapter::getTriples)
              .forEach(model::add);
    }
    var output = Path.of(System.getProperty("user.dir"), "output", "jy_book_chapter.ttl");
    output.toFile().getParentFile().mkdirs();
    RDFWriter.create()
            .set(RIOT.symTurtleDirectiveStyle, "sparql")
            .lang(Lang.TURTLE)
            .format(RDFFormat.TURTLE)
            .source(model)
            .output(output.toString());
  }

  public static List<Chapter> extract(Entry<String, String> entry) throws IOException {

    var book_id = entry.getValue();
    var book_title_zh = entry.getKey();
    var book_key = book_id.toLowerCase().split("_")[0];

    var book = model.createResource(Chapter.JY + book_id, BOOK)
            .addLiteral(RDFS.label, model.createLiteral(book_title_zh, "zh"))
            .addLiteral(Chapter.schema_name, model.createLiteral(book_title_zh, "zh"))
            .addProperty(OWL2.sameAs, String.format(template, book_key));

    var base = String.format(template, book_key);
    var doc = Jsoup.connect(base + "content.htm").get();
    var div = doc.getElementsByTag("td").stream()
            .filter(e -> e.text().replace(" ", "").contains(book_title_zh))
            .findAny().get();
    return div.getElementsByTag("p").stream()
            .filter(e -> e.toString().contains("a href"))
            .map(e -> {
              var number = e.text().split(" ")[0].replace("　", "");
              var ch_url = base + e.getElementsByTag("a").attr("href");
              var t = e.getElementsByTag("a").get(0).text().split("[ 　]");
              var ch_title = t[t.length - 1];
              return new Chapter(book, book_id, number, ch_title, ch_url);
            })
            .collect(Collectors.toList());
  }
}
