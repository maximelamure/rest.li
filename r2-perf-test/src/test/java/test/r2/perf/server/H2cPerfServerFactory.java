/*
   Copyright (c) 2012 LinkedIn Corp.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

/* $Id$ */
package test.r2.perf.server;

import com.linkedin.r2.transport.common.Server;
import com.linkedin.r2.transport.common.bridge.server.TransportDispatcher;
import com.linkedin.r2.transport.http.server.HttpServerFactory;

/**
 * Creates a Jetty {@link Server} with an H2C connector that supports HTTP/1.1 and HTTP/2.0
 * clear text (H2C) through upgrade.
 *
 * @author Sean Sheng
 * @version $Revision$
 */
public class H2cPerfServerFactory extends AbstractPerfServerFactory
{
  @Override
  protected Server createServer(int port, TransportDispatcher dispatcher, boolean restOverStream)
  {
    return new HttpServerFactory().createH2cServer(port, dispatcher, restOverStream);
  }
}