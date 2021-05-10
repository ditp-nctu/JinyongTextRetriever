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
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author Jonathan Chang, Chun-yien <ccy@musicapoetica.org>
 */
public class TextRetriever {

  public static void main(String[] args) throws MalformedURLException, URISyntaxException, IOException, InterruptedException, ParserConfigurationException, SAXException {

    var yi_tian = "https://www.51shucheng.net/zh-tw/wuxia/yitiantulongji";
    var shen_diao = "https://www.51shucheng.net/zh-tw/wuxia/shendiaoxialv";
    var she_diao = "https://www.51shucheng.net/zh-tw/wuxia/shediaoyingxiongzhuan";

    for (String url : new String[]{yi_tian, shen_diao, she_diao}) {
      var doc = Jsoup.connect(url).get();
      var title = doc.getElementsByTag("h1").text();
      System.out.println(title);

      var folder = Path.of(System.getProperty("user.dir"), "downloaded", title);
      folder.toFile().mkdirs();

      var counter = 0;
      for (var li : doc.select("li")) {
        var doc1 = Jsoup.connect(li.getElementsByTag("a").attr("href")).get();
        var title1 = String.format("%02d. %s", ++counter, doc1.getElementsByTag("h1").text());
        System.out.println(title1);

        var content = doc1.getElementById("neirong").getElementsByTag("p").stream()
                .map(Element::text)
                .collect(Collectors.toList());
        Files.write(folder.resolve(title1 + ".txt"), content);
      }
    }
  }
}
