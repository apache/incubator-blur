/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.blur.command;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.blur.command.Command;
import org.apache.blur.command.IndexContext;
import org.apache.blur.command.IndexReadCommand;
import org.apache.blur.command.annotation.Argument;
import org.apache.blur.command.annotation.RequiredArguments;
import org.apache.blur.command.annotation.OptionalArguments;

@SuppressWarnings("serial")
@RequiredArguments({ @Argument(name = "table", value = "The name of the table to execute the wait for N number of seconds command.", type = String.class) })
@OptionalArguments({

    @Argument(name = "shard", value = "The shard id to execute the wait for N number of seconds command.", type = String.class),
    @Argument(name = "seconds", value = "The number of seconds to sleep, the default is 30 seconds.", type = Integer.class)

})
public class WaitForSeconds extends Command implements IndexReadCommand<Boolean> {

  @Override
  public Boolean execute(IndexContext context) throws IOException, InterruptedException {
    Args args = context.getArgs();
    int seconds = args.get("seconds", 30);
    Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
    return true;
  }

  @Override
  public String getName() {
    return "wait";
  }

}
