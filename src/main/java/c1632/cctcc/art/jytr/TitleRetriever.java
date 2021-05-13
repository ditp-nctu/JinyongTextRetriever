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
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
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

  static String template = "https://fpsother.tripod.com/%s/";

  public static void main(String[] args) throws IOException {

    var jy = "http://myjinyongontology.info/";
    var schema = "https://schema.org/";
    var works = Map.of(
            "倚天屠龍記", "Yi_Tian",
            "神 雕 俠 侶", "Shen_Diao",
            "射 雕 英 雄 傳", "She_Diao"
    );

    var model = ModelFactory.createDefaultModel();
    model.setNsPrefix("", "http://myjinyongontology.info/");

    var chapter_of = model.createProperty(schema, "isPartOf");
    var chapter_name = model.createProperty(schema, "name");
    var chapter_number = model.createProperty(jy, "chapter_number");

    var book = model.createResource(schema + "Book");
    var book_chapter = model.createResource(schema + "Chapter");

    for (Entry<String, String> entry : works.entrySet()) {
      System.out.println("Processing " + entry.getKey());
      var book_key = entry.getValue().toLowerCase().split("_")[0];
      var jy_book = model.createResource(jy + entry.getValue(), book)
              .addLiteral(RDFS.label, model.createLiteral(entry.getKey().replace(" ", ""), "zh"))
              .addProperty(OWL2.sameAs, String.format(template, book_key));
      var chapters = extract(entry.getKey(), book_key);
      chapters.stream()
              .peek(System.out::println)
              .forEach(ch -> {
                model.createResource(jy + book_key + "_ch_" + ch.getOrder(), book_chapter)
                        .addProperty(chapter_of, jy_book)
                        .addLiteral(RDFS.label, model.createLiteral(ch.getTitle(), "zh"))
                        .addLiteral(chapter_name, model.createLiteral(ch.getTitle(), "zh"))
                        .addLiteral(chapter_number, model.createTypedLiteral(ch.getOrder()))
                        .addLiteral(OWL2.sameAs, ch.getUrl());
              });
    }
    var output = Path.of(System.getProperty("user.dir"), "output", "jy_book_chapter.ttl");
    output.toFile().getParentFile().mkdirs();
    RDFWriter.create()
            .set(RIOT.symTurtleDirectiveStyle, "sparql")
            .lang(Lang.TTL)
            .source(model)
            .output(output.toString());
  }

  public static List<Chapter> extract(String title, String key) throws IOException {

    var base = String.format(template, key);
    var doc = Jsoup.connect(base + "content.htm").get();
    var div = doc.getElementsByTag("td").stream()
            .filter(e -> e.text().contains(title))
            .findAny().get();
    var result = div.getElementsByTag("p").stream()
            .filter(e -> e.toString().contains("a href"))
            .map(e -> {
              var number = e.text().split(" ")[0].replace("　", "");
              var ch_url = base + e.getElementsByTag("a").attr("href");
              var t = e.getElementsByTag("a").get(0).text().split("[ 　]");
              var ch_title = t[t.length - 1];
              return new Chapter(number, ch_url, ch_title);
            })
            .collect(Collectors.toList());
    return result;
  }
}
