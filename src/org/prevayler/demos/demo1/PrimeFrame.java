// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld, Paul Hammant
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.demos.demo1;

import org.prevayler.implementation.SnapshotPrevayler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;


/**
 * This is client code to the prevalent system. It does not need to be persisted.
 * Only client code that takes the snapshots, like this class, needs to know about the SnapshotPrevayler class.
 * All other client code, like the PrimeCalculator, needs to know only about the Prevayler interface.
 */
class PrimeFrame extends JFrame {

  private final SnapshotPrevayler prevayler;

  private final JLabel primesFoundLabel = new JLabel("", SwingConstants.RIGHT);
  private final JLabel largestPrimeLabel = new JLabel("", SwingConstants.RIGHT);
  private final JLabel candidateLabel = new JLabel("", SwingConstants.RIGHT);
 

  private void takeSnapshot() {
    try {

      prevayler.takeSnapshot();

    } catch (IOException iox) {
      System.out.println("Snapshot failed: " + iox.getMessage());
    }
  }


  void setPrimesFound(int primesFound) {
    primesFoundLabel.setText("" + primesFound);
  }

  void setLargestPrime(int largestPrime) {
    largestPrimeLabel.setText("" + largestPrime);
  }

  void setCandidate(int candidate) {
    candidateLabel.setText("" + candidate);
  }


  PrimeFrame(SnapshotPrevayler prevayler) throws Exception {
    super("Prime Number Calculator");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    this.prevayler = prevayler;

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(numbersPanel(), BorderLayout.CENTER);
    panel.add(snapshotButton(), BorderLayout.SOUTH);

    getContentPane().add(panel);
    pack();
    setVisible(true);

    (new PrimeCalculator(prevayler, this)).start();
  }


  private JPanel numbersPanel() {
    JPanel result = new JPanel(new GridLayout(3,2,10,10));
    result.setBorder(new EmptyBorder(10,10,10,10));

    result.add(new JLabel("Primes Found:"));
    result.add(primesFoundLabel);
    result.add(new JLabel("Largest Prime:"));
    result.add(largestPrimeLabel);
    result.add(new JLabel("Current Candidate:"));
    result.add(candidateLabel);

    return result;
  }


  private JButton snapshotButton() {
    JButton result = new JButton("Take Snapshot");
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {

        takeSnapshot();

      }
    });
    return result;
  }

}
