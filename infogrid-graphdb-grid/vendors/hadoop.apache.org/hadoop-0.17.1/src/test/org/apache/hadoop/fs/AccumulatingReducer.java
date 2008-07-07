/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.UTF8;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TaskTracker;

/**
 * Reducer that accumulates values based on their type.
 * <p>
 * The type is specified in the key part of the key-value pair 
 * as a prefix to the key in the following way
 * <p>
 * <tt>type:key</tt>
 * <p>
 * The values are accumulated according to the types:
 * <ul>
 * <li><tt>s:</tt> - string, concatenate</li>
 * <li><tt>f:</tt> - float, summ</li>
 * <li><tt>l:</tt> - long, summ</li>
 * </ul>
 * 
 */
public class AccumulatingReducer extends MapReduceBase
    implements Reducer<UTF8, UTF8, UTF8, UTF8> {
  
  protected String hostName;
  
  public AccumulatingReducer () {
    TaskTracker.LOG.info("Starting AccumulatingReducer !!!");
    try {
      hostName = java.net.InetAddress.getLocalHost().getHostName();
    } catch(Exception e) {
      hostName = "localhost";
    }
    TaskTracker.LOG.info("Starting AccumulatingReducer on " + hostName);
  }
  
  public void reduce(UTF8 key, 
                     Iterator<UTF8> values,
                     OutputCollector<UTF8, UTF8> output, 
                     Reporter reporter
                     ) throws IOException {
    String field = key.toString();

    reporter.setStatus("starting " + field + " ::host = " + hostName);

    // concatenate strings
    if (field.startsWith("s:")) {
      String sSum = "";
      while (values.hasNext())
        sSum += values.next().toString() + ";";
      output.collect(key, new UTF8(sSum));
      reporter.setStatus("finished " + field + " ::host = " + hostName);
      return;
    }
    // sum long values
    if (field.startsWith("f:")) {
      float fSum = 0;
      while (values.hasNext())
        fSum += Float.parseFloat(values.next().toString());
      output.collect(key, new UTF8(String.valueOf(fSum)));
      reporter.setStatus("finished " + field + " ::host = " + hostName);
      return;
    }
    // sum long values
    if (field.startsWith("l:")) {
      long lSum = 0;
      while (values.hasNext()) {
        lSum += Long.parseLong(values.next().toString());
      }
      output.collect(key, new UTF8(String.valueOf(lSum)));
    }
    reporter.setStatus("finished " + field + " ::host = " + hostName);
  }
}
