package org.lunifera.runtime.utils.osgi.component;

/*
 * #%L
 * Lunifera Subsystems - Runtime Utilities for OSGi
 * %%
 * Copyright (C) 2012 - 2014 C4biz Softwares ME, Loetz KG
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 */
/**
 * 
 * @author cvgaviao
 * @since 0.0.1
 */
public class ExceptionComponentUnrecoveredActivationError extends
		RuntimeException {

	private static final long serialVersionUID = 4800465886577999271L;

	public ExceptionComponentUnrecoveredActivationError() {
		super();
	}

	/**
	 * Construct a new ExceptionRuntimeAddon with the specified message.
	 *
	 * @param message
	 *            The message for the exception.
	 */
	public ExceptionComponentUnrecoveredActivationError(String message) {
		super(message);
	}

	/**
	 * Construct a new ExceptionRuntimeAddon with the specified message and
	 * cause.
	 *
	 * @param message
	 *            The message for the exception.
	 * @param cause
	 *            The cause of the exception. May be {@code null}.
	 */
	public ExceptionComponentUnrecoveredActivationError(String message,
			Throwable cause) {
		super(message, cause);
	}

	/**
	 * Construct a new ExceptionRuntimeAddon with the specified cause.
	 *
	 * @param cause
	 *            The cause of the exception. May be {@code null}.
	 */
	public ExceptionComponentUnrecoveredActivationError(Throwable cause) {
		super(cause);
	}
}
