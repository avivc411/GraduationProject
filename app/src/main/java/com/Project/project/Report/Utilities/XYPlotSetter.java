package com.Project.project.Report.Utilities;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;

import androidx.annotation.NonNull;

import com.Project.project.R;
import com.androidplot.ui.HorizontalPositioning;
import com.androidplot.ui.VerticalPositioning;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PanZoom;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XYPlotSetter {
    private final static int[] linePointFormatter = {
            R.xml.line_point_formatter_with_labels_0,
            R.xml.line_point_formatter_with_labels_1,
            R.xml.line_point_formatter_with_labels_2,
            R.xml.line_point_formatter_with_labels_3,
            R.xml.line_point_formatter_with_labels_4,
            R.xml.line_point_formatter_with_labels_5,
            R.xml.line_point_formatter_with_labels_6,
            R.xml.line_point_formatter_with_labels_7,
            R.xml.line_point_formatter_with_labels_8,
            R.xml.line_point_formatter_with_labels_9,
    };

    public static void setPlot(Activity activity,
                               XYPlot plot,
                               Map<String, List<Integer>> variableToValues,
                               final List<String> domainLabels,
                               int domainSize,
                               int rangeSize,
                               String title,
                               String rangeTitle,
                               String domainTitle,
                               boolean zoom,
                               boolean showLegend,
                               boolean showTitle,
                               boolean showDomainTitle,
                               boolean showRangeTitle,
                               boolean showDomainLabels,
                               boolean showRangeLabels,
                               boolean scaleRange,
                               float domainLabelRotation) {
        if (plot == null)
            throw new IllegalArgumentException("Plot is null");

        plot.clear();

        //titles
        if (showTitle)
            plot.setTitle(title);
        if (showDomainTitle)
            plot.setDomainLabel(domainTitle);
        if (showRangeTitle)
            plot.setRangeLabel(rangeTitle);

        if (variableToValues == null || variableToValues.size() == 0) {
            showDefault(plot, activity);
            return;
        }

        //enable zooming - instead of swipe!
        if (zoom) {
            plot.getOuterLimits().set(0, domainSize, 0, rangeSize + 1);
            PanZoom.attach(plot, PanZoom.Pan.HORIZONTAL, PanZoom.Zoom.STRETCH_HORIZONTAL);
        }

        //display legend
        if (showLegend) {
            plot.getLegend().setVisible(true);
            plot.getLegend().position(PixelUtils.dpToPix(10), HorizontalPositioning.ABSOLUTE_FROM_LEFT,
                    PixelUtils.dpToPix(30), VerticalPositioning.ABSOLUTE_FROM_BOTTOM);
        }

        //text formatter - score is beneath it's point, color is BLACK and text is BOLD
        PointLabelFormatter plf = new PointLabelFormatter();
//        plf.vOffset = 50;
        plf.hOffset = 30;
        plf.getTextPaint().setColor(Color.BLACK);
        plf.getTextPaint().setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        //remove domain labels
        if (!showDomainLabels)
            plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).getPaint().setColor(Color.TRANSPARENT);
        else
            showDomainLabels(plot, domainLabels, domainLabelRotation);

        //remove range labels
        if (!showRangeLabels) {
            plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).getPaint().setColor(Color.TRANSPARENT);
            plot.getGraph().setMarginLeft(-30);
            plot.getGraph().setMarginRight(30);
        }

        //range (y) grid lines
        plot.getGraph().setLinesPerRangeLabel(rangeSize + 1);

        //range scale intervals
        // In order to match Y scale to current data, remove comment from for loop or find max value independently.
        int max = -1;
        if (scaleRange)
            for (Map.Entry entry : variableToValues.entrySet())
                for (int score : (List<Integer>) entry.getValue())
                    if (score > max)
                        max = score;
        plot.setRangeStep(StepMode.SUBDIVIDE, (max == -1 ? rangeSize + 2 : max + 2));
        plot.setRangeBoundaries(0, (max == -1 ? rangeSize + 1 : max + 1), BoundaryMode.FIXED);

        //domain grid intervals
        //add one to domain in order to uncut the last point
        plot.setDomainStep(StepMode.SUBDIVIDE, domainSize + 1);
        plot.setDomainBoundaries(0, domainSize, BoundaryMode.FIXED);

        addSeries(plot, activity, variableToValues, plf);

        plot.redraw();
    }

    /**
     * Set domain labels style.
     */
    private static void showDomainLabels(XYPlot plot, List<String> domainLabels, float domainLabelRotation) {
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).getPaint().setColor(Color.BLACK);
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setRotation(domainLabelRotation);
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, @NonNull StringBuffer toAppendTo, @NonNull FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                String label;
                try {
                    label = domainLabels.get(i);
                } catch (IndexOutOfBoundsException e) {
                    label = "";
                }
                return toAppendTo.append(label);
            }

            @Override
            public Object parseObject(String source, @NonNull ParsePosition pos) {
                return null;
            }
        });
    }

    /**
     * Create series for every variable.
     */
    private static void addSeries(XYPlot plot,
                                  Activity activity,
                                  Map<String, List<Integer>> variableToValues,
                                  PointLabelFormatter plf) {
        int i = 0;
        for (Map.Entry entry : variableToValues.entrySet()) {
            XYSeries series = new SimpleXYSeries(
                    (List<Integer>) entry.getValue(),
                    SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                    (String) entry.getKey());

            LineAndPointFormatter seriesFormat =
                    new LineAndPointFormatter(activity, linePointFormatter[i++]);
            if (((List<Integer>) entry.getValue()).size() >= 3)
                seriesFormat.setInterpolationParams(
                        new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

            seriesFormat.setPointLabelFormatter(plf);

            plot.addSeries(series, seriesFormat);
        }
    }

    private static void showDefault(XYPlot plot, Activity activity) {
        plot.clear();
        addSeries(plot, activity, new HashMap<>(), null);
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).getPaint().setColor(Color.TRANSPARENT);
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).getPaint().setColor(Color.TRANSPARENT);
        plot.getGraph().setMarginLeft(-30);
        plot.getGraph().setMarginRight(30);
        plot.redraw();
    }
}
