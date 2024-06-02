package com.example.fyp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class DynamicHorizontalBarChart extends JFrame {

    public DynamicHorizontalBarChart(String title, Map<String, Double> data) {
        super(title);
        JFreeChart barChart = createChart(createDataset(data), data);
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(1000, 600));
        
        // Create a JPanel to hold the chart and the largest emotion label
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new java.awt.BorderLayout());
        contentPanel.add(chartPanel, java.awt.BorderLayout.CENTER);
        
        // Create a JTextField to display the largest emotion at the bottom
        String largestEmotionText = getLargestEmotionText(data);
        JTextField largestEmotionLabel = new JTextField(largestEmotionText);
        largestEmotionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        largestEmotionLabel.setEditable(false);
        largestEmotionLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        contentPanel.add(largestEmotionLabel, java.awt.BorderLayout.SOUTH);
        
        setContentPane(contentPanel);
    }

    private DefaultCategoryDataset createDataset(Map<String, Double> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            dataset.addValue(entry.getValue(), "Emotion", entry.getKey());
        }
        return dataset;
    }

    private JFreeChart createChart(DefaultCategoryDataset dataset, Map<String, Double> data) {
        JFreeChart chart = ChartFactory.createBarChart(
                "Dynamic Horizontal Bar Chart",      // chart title
                "",                           // domain axis label
                "",                        // No range axis label
                dataset,                             // data
                PlotOrientation.HORIZONTAL,          // orientation
                false,                                // include legend
                false,                                // tooltips
                false                                // urls
        );

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setDomainGridlinesVisible(false);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(79, 129, 189));

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryMargin(0.15);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // Set the background color of the plot
        plot.setBackgroundPaint(Color.WHITE);

   
        // Calculate the total sum of values
        double total = data.values().stream().mapToDouble(Double::doubleValue).sum();

        // Add percentage annotations to each bar and find the largest emotion
        String largestEmotion = " ";
        double maxPercentage = 0;
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            double value = entry.getValue();
            double percentage = (value / total) * 100;
            String annotationText = String.format("%.2f%%", percentage);
            CategoryTextAnnotation annotation = new CategoryTextAnnotation(annotationText, entry.getKey(), value);
            annotation.setFont(new Font("SansSerif", Font.PLAIN, 12));

            // Adjust the x-coordinate of the annotation to place it fully outside the bar
            double xCoordinate = value + 1; // Move the annotation to the right of the bar
            annotation.setCategory(entry.getKey());
            annotation.setValue(xCoordinate);
            plot.addAnnotation(annotation);

            if (percentage > maxPercentage) {
                maxPercentage = percentage;
                largestEmotion = entry.getKey();
            }
        }


        // Construct the text for the largest emotion at the bottom
        String MonthlyAnalysis = "Montly Analysis";

        // Set the chart title
        chart.setTitle(MonthlyAnalysis);

        return chart;
    }

    private String getLargestEmotionText(Map<String, Double> data) {
        // Calculate the total sum of values
        double total = data.values().stream().mapToDouble(Double::doubleValue).sum();

        // Find the largest emotion and its percentage
        String largestEmotion = "";
        double maxPercentage = 0;
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            double value = entry.getValue();
            double percentage = (value / total) * 100;
            if (percentage > maxPercentage) {
                maxPercentage = percentage;
                largestEmotion = entry.getKey();
            }
        }

        // Construct the text for the largest emotion
        return "The largest emotion felt this month is " + largestEmotion + " at " + String.format("%.2f%%", maxPercentage);
    }

    public static void main(String[] args) {

        //Add all the emotions felt in the month accordingly (based on all diary records)
        String diary;


        // Example data (Or all the emotions felt in the month)
        Map<String, Double> data = Map.of(
                "Happiness", 10.0,
                "Sadness", 20.0,
                "Fear", 15.0,
                "Surprise", 25.0,
                "Anger", 30.0
        );

        DynamicHorizontalBarChart chart = new DynamicHorizontalBarChart("Monthly Analysis", data);
        chart.setSize(1000, 600);
        chart.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chart.setLocationRelativeTo(null); // Centers the frame on the screen
        chart.setVisible(true);
    }
}
