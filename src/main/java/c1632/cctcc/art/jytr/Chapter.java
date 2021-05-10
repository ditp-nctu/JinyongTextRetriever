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
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 *
 * @author Jonathan Chang, Chun-yien <ccy@musicapoetica.org>
 */
@AllArgsConstructor
@Getter
@ToString
public class Chapter {

  private String number;
  private String url;
  private String title;

  public int getOrder() {

    try {
      return Integer.valueOf(new URL(url).getFile().replace(".htm", "").split("/")[2]);
    } catch (MalformedURLException ex) {
      Logger.getLogger(Chapter.class.getName()).log(Level.SEVERE, null, ex);
    }
    return -1;
  }
}
